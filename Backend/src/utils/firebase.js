var admin = require("firebase-admin");
var serviceAccount = require("./socialqs-config.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
})

module.exports = admin