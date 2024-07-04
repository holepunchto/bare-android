cmake_minimum_required(VERSION 3.25)

project(bare_android C)

include(bare)

set(BARE_ANDROID_USE_LOGCAT ON CACHE BOOL "Use Logcat for logging" FORCE)

install_node_modules(WORKING_DIRECTORY ..)

add_subdirectory(../vendor/bare-kit vendor/bare-kit EXCLUDE_FROM_ALL)

add_bare_bundle(
  bare_android_bundle
  ENTRY app/src/main/js/app.js
  OUT app/src/main/assets/app.bundle
  WORKING_DIRECTORY ..
)

add_library(bare_android SHARED)

set_target_properties(
  bare_android
  PROPERTIES
  C_STANDARD 99
  POSITION_INDEPENDENT_CODE ON
)

add_dependencies(bare_android bare_android_bundle)

target_link_libraries(
  bare_android
  PUBLIC
    bare_kit
)

link_bare_modules(bare_android WORKING_DIRECTORY ..)
