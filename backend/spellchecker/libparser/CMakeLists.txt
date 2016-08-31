cmake_minimum_required(VERSION 3.2)
project(parsers)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -lhunspell")

set(SOURCE_FILES 
    parsers/latexparser.cxx
    parsers/latexparser.hxx
    parsers/textparser.cxx
    parsers/textparser.hxx
    parser.cpp)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/parsers)

add_library(parser SHARED ${SOURCE_FILES})
