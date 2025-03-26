console.log('Hello Android notifcations (call)!')

BareKit.on('push', (payload, reply) => {
  console.log('Notification received:', JSON.parse(payload))

  reply(
    null,
    JSON.stringify({
      type: 'call',
      title: 'Notification received',
      body: 'This is the body',
      caller: 'tony'
    })
  )
})
