const Koa = require('koa')
const KoaRouter = require('koa-router')
const config = require('./config')
const COSSDK = require('cos-nodejs-sdk-v5');
const authorization = require('auth-header');
const fetch = require('node-fetch');

const app = new Koa()
const router = new KoaRouter()

const cos = new COSSDK({
  SecretId: config.cos.secretId,
  SecretKey: config.cos.secretKey
});

const cosBucket = config.cos.bucket;
const cosRegion = config.cos.region;

// Get COS presigned url
async function getPreSignedURL(key) {
  return new Promise((resolve, reject) => {
    cos.getObjectUrl({
      Bucket: cosBucket,
      Region: cosRegion,
      Key: key,
      Expires: 3600
    }, (err, data) => {
      if (err) {
        return reject(err);
      }

      resolve(data.Url)
    })
  })
}

// verify repo user
async function verifyRepoUser(userid, repo, un, pw) {
  return fetch(config.api_url, {
    method: 'post',
    body: JSON.stringify({
      'ownerId': userid,
      'repo': repo,
      'userName': un,
      'password': pw
    }),
    headers: { 'Content-Type': 'application/json' },
  })
    .then(res => res.json())
    .then(json => {
      if (json.flag !== true) throw json.message;
      return json.data;
    })
    .catch(e => { throw new Error(`User validation failed. Reason: ${e.message}`); });
}

function parseAuth(header) {
  try {
    var auth = authorization.parse(header);
  } catch (e) {
    throw 'Invalid header';
  }

  if (auth.scheme !== 'Basic') {
    throw 'Invalid authorization scheme';
  }

  // Get the basic auth component.
  var [un, pw] = Buffer.from(auth.token, 'base64').toString().split(':', 2);
  console.log(un, pw);

  return [un, pw];
}

// Routes
// Match all repo files download
router.get(`/:user_id/:repo/:file(.*).:ext`, async (ctx) => {
  // http basic auth
  console.log(ctx)
  try {
    var [un, pw] = parseAuth(ctx.get('authorization'));

    let result = await verifyRepoUser(ctx.params.user_id, ctx.params.repo, un, pw);
    if (result !== true) throw 'Invalid user or password';
  } catch (e) {
    ctx.status = 401;
    ctx.set('WWW-Authenticate', 'Basic');
    ctx.body = `Authentication required. Reason: ${e.message}`;
    return;
  }

  // redirect to presigned object url
  let objectKey = ctx.path;
  try {
    let url = await getPreSignedURL(objectKey);
    let headers = {
      'Access-Control-Allow-Origin': '*',
    };
    ctx.set(headers);
    ctx.redirect(url);
  } catch (e) {
    ctx.status = 400;
    ctx.set({ "Content-Type": "text/plain" });
    ctx.body = e;
  }
})

// Match all route
router.get(`/*`, async (ctx) => {
  ctx.status = 400;
  ctx.set({ "Content-Type": "text/plain" });
  ctx.body = 'Invalid URL.';
})


app.use(router.allowedMethods()).use(router.routes());

// don't forget to export!
module.exports = app;
