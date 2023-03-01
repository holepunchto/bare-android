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
