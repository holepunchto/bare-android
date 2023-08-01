#include <assert.h>
#include <bare.h>
#include <jni.h>
#include <uv.h>

#include "MainActivity.bundle.h"

JNIEXPORT void JNICALL
Java_com_holepunch_bare_MainActivity_init (JNIEnv *env, jobject self) {
  int err;

  int argc = 0;
  char **argv = NULL;

  argv = uv_setup_args(argc, argv);

  bare_t *bare;
  err = bare_setup(uv_default_loop(), argc, argv, NULL, &bare);
  assert(err == 0);

  uv_buf_t source = uv_buf_init((char *) bundle, bundle_len);

  err = bare_run(bare, "/main.bundle", &source);
  assert(err == 0);

  int exit_code;
  err = bare_teardown(bare, &exit_code);
  assert(err == 0);
}