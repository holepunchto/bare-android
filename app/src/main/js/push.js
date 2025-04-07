console.log('Hello Android notifications!')

BareKit.on('push', (payload, reply) => {
  console.log('Notification received:', JSON.parse(payload))

  reply(
    null,
    JSON.stringify({
      title: 'Notification received',
      body: 'This is the body'
    })
  )
})
