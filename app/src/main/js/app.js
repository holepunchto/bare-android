/* global Bare */

Bare
  .on('suspend', () => console.log('suspended'))
  .on('resume', () => console.log('resumed'))
  .on('exit', () => console.log('exited'))

Bare.IPC
  .on('data', (data) => console.log(data.toString()))
  .write('Hello from Bare')
