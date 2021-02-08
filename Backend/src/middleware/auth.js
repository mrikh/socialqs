const jwt = require('jsonwebtoken')
const User = require('../models/user')
const constants = require('../utils/constants')

const auth = async (req, res, next) => {
    
    try{

        const authValue = req.header('Authorization')
        if (!authValue){
            const error = new Error(constants.authenticate)
            error.statusCode = 405
            throw error
        }

        const token = authValue.replace('Bearer ', '')

        const decoded = jwt.verify(token, process.env.JWT_SECRET)
        //will search inside tokens array for token property
        const user = await User.findOne({_id : decoded._id, token})

        if (!user){ 
            const error = new Error(constants.user_not_found)
            error.statusCode = 405
            throw error
        }

        //store for access later on
        req.user = user
        req.token = token
        next()
    }catch(error){
        next(error)
    }
}

module.exports = auth