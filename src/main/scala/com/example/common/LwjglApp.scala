/*******************************************************************************
 * Copyright 2015 Serf Productions, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.example.common

import org.lwjgl.glfw._, GLFW._
import org.lwjgl.opengl._
import org.lwjgl.system.MemoryUtil._
import org.lwjgl.system.NativeResource

abstract class LwjglApp(private val al: ApplicationListener) {
  def title:  String
  def width:  Int
  def height: Int

  protected def windowHints: Map[Int, Int]
  protected def setupCallbacks(window: Long): List[NativeResource]

  def run(): Unit = {
    initGlfw()

    try {
      val window    = createWindow()
      val callbacks = setupCallbacks(window)

      showWindow(window)

      GL.createCapabilities()
      al.init()

      loop(window)

      al.dispose()

      glfwDestroyWindow(window)
      callbacks.foreach(_.close())
    } finally {
      glfwTerminate() // destroys all remaining windows, cursors, etc...
      glfwSetErrorCallback(null).free()
    }
  }

  protected def initGlfw(): Unit = {
    GLFWErrorCallback.createPrint(System.err).set()

    if (!glfwInit())
      throw new IllegalStateException("Unable to initialize GLFW")
  }

  protected def createWindow(): Long = {
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    windowHints foreach { case (key, value) =>
      glfwWindowHint(key, value)
    }

    val window = glfwCreateWindow(width, height, title, NULL, NULL)
    if (window == NULL)
      throw new RuntimeException("Failed to create the GLFW Window")

    window
  }

  protected def showWindow(window: Long): Unit = {
    val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())

    glfwSetWindowPos (
      window,
      (vidMode.width()  - width)  / 2,
      (vidMode.height() - height) / 2
    )

    glfwMakeContextCurrent(window)
    glfwSwapInterval(1)
    glfwShowWindow(window)
  }

  protected def loop(window: Long): Unit =
    while (!glfwWindowShouldClose(window)) {
      al.update(glfwGetTime())
      al.render()

      glfwSwapBuffers(window)
      glfwPollEvents()
    }
}