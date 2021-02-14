var admin = require("firebase-admin");

var serviceAccount = require("./socialqs-config.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
})

const sendPush = (user, title, body) => {

    const message = {
        notification : {
            title : title,
            body : body
        },
        token : user.pushToken
    }

    admin.messaging().sendToDevice(registrationToken, message, options).then( response => {
        console.log("Notification sent successfully")
    }).catch( error => {
        console.log(error);
    });   
}

module.exports = sendPush