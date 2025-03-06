console.log('Hello from push worklet!')

BareKit.on('push', (json, reply) => {
  console.log('Push notification received:', json.toString())

  reply(
    null,
    JSON.stringify({
      title: 'Push notification received',
      body: 'this is the body'
    })
  )
})
