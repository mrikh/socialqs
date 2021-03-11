const mongoose = require('mongoose')
const constants = require('../utils/constants')
const validator = require('validator')

const questionSchema = new mongoose.Schema({
    title : {
        type : String,
        required : [true, constants.invalid_title],
        trim : true,
        validate(value){
            if (validator.isEmpty(value)){
                throw new Error(constants.invalid_title)
            }
        }
    },
    creator : {
        type : mongoose.Schema.Types.ObjectId,
        required : true,
        ref : 'User'
    },
    category : {
        type : mongoose.Schema.Types.ObjectId,
        required : true,
        ref : 'Category'
    },
    videoUrl : {
        type : String,
        required : true,
        validate(value){
            if (!validator.isURL(value)){
                throw new Error(constants.invalid_url)
            }
        }
    },
    thumbnailUrl : {
        type : String,
        validate(value){
            if (!validator.isURL(value)){
                throw new Error(constants.invalid_url)
            }
        }
    },
    bookmarkedBy : [{
        type : mongoose.Schema.Types.ObjectId,
        ref : 'User'   
    }]
},{
    timestamps : true
})

questionSchema.methods.toJSON = function() {

    const questionObject = this.toObject()
    delete questionObject.__v
    delete questionObject.createdAt
    delete questionObject.updatedAt
    return questionObject
}


const Question = mongoose.model('Question', questionSchema)
module.exports = Question