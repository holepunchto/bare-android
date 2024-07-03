/* global Bare, BareKit */

Bare
  .on('suspend', () => console.log('suspended'))
  .on('resume', () => console.log('resumed'))
  .on('exit', () => console.log('exited'))

const rpc = new BareKit.RPC((req) => {
  if (req.command === 'ping') {
    console.log(req.data.toString())

    req.reply('Pong from Bare')
  }
})

const req = rpc.request('ping')

req.send('Ping from Bare')

req.reply().then((data) => console.log(data.toString()))
