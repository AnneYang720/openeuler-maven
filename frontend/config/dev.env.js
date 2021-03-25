'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  //BASE_API: '"http://easy-mock.anneyang.me/mock/60166a81e235063d55064468"',
  BASE_API: '"http://127.0.0.1:9001"',
})
