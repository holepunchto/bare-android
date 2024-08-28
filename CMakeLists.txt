cmake_minimum_required(VERSION 3.25)

project(bare_android C)

include(bare)

install_node_modules()

add_bare_bundle(
  bare_android_bundle
  ENTRY app/src/main/js/app.js
  OUT app/src/main/assets/app.bundle
)

add_library(bare_android SHARED)

add_dependencies(bare_android bare_android_bundle)

set_target_properties(
  bare_android
  PROPERTIES
  LINKER_LANGUAGE C
  OUTPUT_NAME app
)

target_sources(
  bare_android
  PUBLIC
    app/src/main/assets/app.bundle
)

link_bare_modules(
  bare_android
  PREBUILDS
  DESTINATION app/src/main/jniLibs/${ANDROID_ABI}
)

find_package(bare-kit REQUIRED CONFIG)

target_link_libraries(
  bare_android
  PUBLIC
    bare-kit::bare_kit
)
