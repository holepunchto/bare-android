cmake_minimum_required(VERSION 3.25)

project(bare_android C)

include(bare)

set(bare_android_root ../../../..)

install_node_modules(WORKING_DIRECTORY ${bare_android_root})

add_bare_bundle(
  ENTRY ../js/app.js
  OUT ../assets/app.bundle
)

add_custom_target(
  bare_android
  ALL
  DEPENDS ../assets/app.bundle
)
