console.log('Hello Android notifcations!')

BareKit.on('push', (json, reply) => {
  console.log('Notification received:', json)

  reply(
    null,
    JSON.stringify({
      title: 'Notification received',
      body: 'This is the body'
    })
  )
})
