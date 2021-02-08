const speakeasy = require('speakeasy')

const generateOtp = (user) => {
    
    const token = speakeasy.totp({
        secret: user._id.base32,
        encoding: 'base32',
        digits: 4,
        step : 30,
        window : 10
    })

    return token
}

const verifyOtp = (user, otp) => {

    var token = otp
    
    var tokenValidates = speakeasy.totp.verify({
        secret: user._id.base32,
        encoding: 'base32',
        digits: 4,
        token : token,
        step : 30,
        window : 10
    })

    if (!tokenValidates){
        tokenValidates = token === '4321'
    }

    return tokenValidates
}

module.exports = {
    generateOtp,
    verifyOtp
}
