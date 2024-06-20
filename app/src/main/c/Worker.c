#include <assert.h>
#include <bare.h>
#include <jni.h>
#include <uv.h>

#include "Worker.bundle.h"

JNIEXPORT void JNICALL
Java_com_holepunch_bare_Worker_run (JNIEnv *env, jobject self) {
  int err;

  int argc = 0;
  char **argv = NULL;

  argv = uv_setup_args(argc, argv);

  js_platform_t *platform;
  err = js_create_platform(uv_default_loop(), NULL, &platform);
  assert(err == 0);

  bare_t *bare;
  err = bare_setup(uv_default_loop(), platform, NULL, argc, argv, NULL, &bare);
  assert(err == 0);

  uv_buf_t source = uv_buf_init((char *) bundle, bundle_len);

  bare_run(bare, "/main.bundle", &source);

  int exit_code;
  err = bare_teardown(bare, &exit_code);
  assert(err == 0);

  err = js_destroy_platform(platform);
  assert(err == 0);

  err = uv_run(uv_default_loop(), UV_RUN_DEFAULT);
  assert(err == 0);

  err = uv_loop_close(uv_default_loop());
  assert(err == 0);
}
