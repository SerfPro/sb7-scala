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
import org.lwjgl.opengl._, GL11._
import org.lwjgl.system.NativeResource

abstract class DefaultLwjglApp(_al: ApplicationListener)
extends LwjglApp(_al) {
  import CallbackHelpers._

  override protected val windowHints: Map[Int, Int] =
    Map (
      GLFW_RESIZABLE             -> GL_FALSE,
      GLFW_CONTEXT_VERSION_MAJOR -> 4,
      GLFW_CONTEXT_VERSION_MINOR -> 1,
      GLFW_OPENGL_FORWARD_COMPAT -> GL_TRUE,
      GLFW_OPENGL_PROFILE        -> GLFW_OPENGL_CORE_PROFILE
    )

  override protected def setupCallbacks(window: Long)
  : List[NativeResource] = {
    val keyCB: GLFWKeyCallback = GLFWKeyCallback.create(keyHandler _)
    glfwSetKeyCallback(window, keyHandler _)

    keyCB :: Nil
  }

  protected def keyHandler (
    window: Long, key: Int, scanCode: Int, action: Int, mods: Int
  ): Unit =
    if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
      glfwSetWindowShouldClose(window, true)
}
