const express = require('express')
require('./db/mongoose')
const notificationsRouter = require('./routers/notification.js')
const followRouter = require('./routers/follow.js')
const answerRouter = require('./routers/answer.js')
const questionsRouter = require('./routers/questions.js')
const userRouter = require('./routers/user')
const errorHandler = require('./middleware/error')
const constants = require('./utils/constants')
const pushClass = require('./utils/firebase')


const app = express()
const port = process.env.PORT

app.use(express.json())
//routers
app.use(notificationsRouter)
app.use(followRouter)
app.use(answerRouter)
app.use(questionsRouter)
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