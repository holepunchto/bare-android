console.log('Hello Android notifications (call)!')

BareKit.on('push', (payload, reply) => {
  const { data } = JSON.parse(payload)
  console.log('Notification received:', data)

  switch (data.type) {
    // Push notification
    case 'notification':
      return reply(
        null,
        JSON.stringify({
          type: 'notification',
          title: data.title,
          body: data.body
        })
      )

    // VoIP notification
    case 'call':
      return reply(
        null,
        JSON.stringify({
          type: 'call',
          caller: data.caller,
          id: data.id
        })
      )

    default:
      return reply(null, null)
  }
})
