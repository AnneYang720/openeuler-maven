export async function pFileReader(file) {
    return new Promise((resolve, reject) => {
        var fr = new FileReader();  
        fr.onload = resolve;  // CHANGE to whatever function you want which would eventually call resolve
        fr.readAsBinaryString(file);
    });
}
