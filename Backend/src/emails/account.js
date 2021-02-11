const sgMail  = require('@sendgrid/mail')
const otpHandler = require('../utils/otp')

sgMail.setApiKey(process.env.SENDGRID_API_KEY)

const sendVerificationMail = (user) => {

    const token = otpHandler.generateOtp(user)
    
    sgMail.send({
        to: user.email,
        from: 'mayankrikh@gmail.com',
        subject : 'Welcome!',
        text : `Welcome ${user.name}. Please enter ${token} to verify your account.`
    })
}

const sendForgotMail = (user) => {
    
    const token = otpHandler.generateOtp(user)

    sgMail.send({
        to : user.email,
        from : 'mayankrikh@gmail.com',
        subject: 'Forgot Password',
        text : `Please enter ${token} to verify your account.`
    })
}

module.exports = {
    sendVerificationMail,
    sendForgotMail
}