const mongoose = require('mongoose')
const constants = require('../utils/constants')

const categorySchema = new mongoose.Schema({
    name : {
        type : String,
        required : [true, constants.invalid_name],
        trim : true,
        validate(value){
            if (!value || '' === value){
                throw new Error(constants.invalid_name)
            }
        }
    }
},{
    timestamps : true
})

categorySchema.methods.toJSON = function() {
    const categoryObject = this.toObject()
    delete categoryObject.__v
    delete categoryObject.createdAt
    delete categoryObject.updatedAt
    return categoryObject
}

const Category = mongoose.model('Category', categorySchema)
module.exports = Category