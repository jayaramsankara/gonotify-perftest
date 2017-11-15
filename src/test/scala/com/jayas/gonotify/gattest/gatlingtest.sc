package com.jayas.gonotify.gattest

object gatlingtest {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  val test123 = "123"                             //> test123  : String = 123
  
  
  object testObj {
    val cnt = 10
  }
  
  def test  = {
     (10,"test string")
  }                                               //> test: => (Int, String)
  val testRet = test                              //> testRet  : (Int, String) = (10,test string)
}