cmake_minimum_required(VERSION 3.25)

execute_process(
  COMMAND pear-dev paths cmake
  OUTPUT_VARIABLE pear_cmake
)

list(APPEND CMAKE_MODULE_PATH ${pear_cmake})

project(
  pear_android
  VERSION 1.0
  LANGUAGES C CXX
)

include(pear)

set(PEAR_MODULE_DIR ${PROJECT_SOURCE_DIR}/../../../..)

add_subdirectory(
  ${PEAR_MODULE_DIR}/vendor/pearjs
  vendor/pearjs
  EXCLUDE_FROM_ALL
)

add_library(pear_android OBJECT)

set_target_properties(
  pear_android
  PROPERTIES
  C_STANDARD 99
  POSITION_INDEPENDENT_CODE ON
)

add_pear_bundle(
  CWD ..
  ENTRY js/app.js
  OUT c/MainActivity.bundle.h
  TARGET c
)

target_sources(
  pear_android
  PRIVATE
    MainActivity.bundle.h
    MainActivity.c
)

target_include_directories(
  pear_android
  PUBLIC
    $<TARGET_PROPERTY:pear,INTERFACE_INCLUDE_DIRECTORIES>
)

find_library(log log)

add_library(pear_android_shared SHARED $<TARGET_OBJECTS:pear_android>)

target_link_libraries(
  pear_android_shared
  PRIVATE
    ${log}
    $<LINK_LIBRARY:WHOLE_ARCHIVE,pear_static>
)
