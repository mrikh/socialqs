const express = require('express')
require('./db/mongoose')
const userRouter = require('./routers/user')
const errorHandler = require('./middleware/error')
const constants = require('./utils/constants')

const app = express()
const port = process.env.PORT

app.use(express.json())
//routers
app.use(userRouter)

//extra end points if hit
app.use('*', (req, res, next) => {
    const error = new Error(constants.url_not_exist)
    error.statusCode = 404
    next(error)
})

//error handler middleware
app.use(errorHandler)

app.listen(port, () => {
    
})