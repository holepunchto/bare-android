cmake_minimum_required(VERSION 3.25)

project(bare_android C)

include(bare)

install_node_modules(
  WORKING_DIRECTORY ../../../..
)

add_subdirectory(
  ../../../../vendor/bare
  vendor/bare
  EXCLUDE_FROM_ALL
)

add_library(bare_android OBJECT)

set_target_properties(
  bare_android
  PROPERTIES
  C_STANDARD 11
  POSITION_INDEPENDENT_CODE ON
)

add_bare_bundle(
  ENTRY app/src/main/js/app.js
  OUT app/src/main/c/Worker.bundle.h
  TARGET c
  WORKING_DIRECTORY ../../../..
)

target_sources(
  bare_android
  PRIVATE
    Worker.bundle.h
    Worker.c
)

target_include_directories(
  bare_android
  PUBLIC
    $<TARGET_PROPERTY:bare,INTERFACE_INCLUDE_DIRECTORIES>
)

add_library(bare_android_shared SHARED)

target_link_libraries(
  bare_android_shared
  PUBLIC
    bare_android
  PRIVATE
    $<LINK_LIBRARY:WHOLE_ARCHIVE,bare_static>
)

link_bare_modules(
  bare_android_shared
  WORKING_DIRECTORY ../../../..
)
