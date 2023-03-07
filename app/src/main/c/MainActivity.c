#include <assert.h>
#include <jni.h>
#include <pear.h>
#include <uv.h>

#include "MainActivity.bundle.h"

JNIEXPORT void JNICALL
Java_com_holepunch_pear_MainActivity_init (JNIEnv *env, jobject self) {
  int err;

  int argc = 0;
  char **argv = NULL;

  argv = uv_setup_args(argc, argv);

  pear_t pear;
  err = pear_setup(uv_default_loop(), &pear, argc, argv);
  assert(err == 0);

  uv_buf_t source = uv_buf_init((char *) bundle, bundle_len);

  err = pear_run(&pear, "/main.bundle", &source);
  assert(err == 0);

  int exit_code;
  err = pear_teardown(&pear, &exit_code);
  assert(err == 0);
}