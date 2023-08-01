cmake_minimum_required(VERSION 3.25)

execute_process(
  COMMAND bare-dev paths cmake
  OUTPUT_VARIABLE bare_cmake
)

list(APPEND CMAKE_MODULE_PATH ${bare_cmake})

project(
  bare_android
  VERSION 1.0
  LANGUAGES C CXX
)

include(bare)

set(BARE_MODULE_DIR ${PROJECT_SOURCE_DIR}/../../../..)

add_subdirectory(
  ${BARE_MODULE_DIR}/vendor/bare
  vendor/bare
  EXCLUDE_FROM_ALL
)

add_library(bare_android OBJECT)

set_target_properties(
  bare_android
  PROPERTIES
  C_STANDARD 99
  POSITION_INDEPENDENT_CODE ON
)

add_pear_bundle(
  CWD ${BARE_MODULE_DIR}
  ENTRY app/src/main/js/app.js
  OUT app/src/main/c/MainActivity.bundle.h
  TARGET c
)

target_sources(
  bare_android
  PRIVATE
    MainActivity.bundle.h
    MainActivity.c
)

target_include_directories(
  bare_android
  PUBLIC
    $<TARGET_PROPERTY:bare,INTERFACE_INCLUDE_DIRECTORIES>
)

find_library(log log)

add_library(bare_android_shared SHARED $<TARGET_OBJECTS:bare_android>)

target_link_libraries(
  bare_android_shared
  PRIVATE
    ${log}
    $<LINK_LIBRARY:WHOLE_ARCHIVE,bare_static>
)
