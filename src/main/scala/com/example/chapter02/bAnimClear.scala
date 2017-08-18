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

package com.example.chapter02

import scala.math.{sin, cos}

import org.lwjgl.opengl._, GL11._, GL30.glClearBufferfv
import org.lwjgl.system.MemoryUtil.memFree

import com.example.common.{ApplicationListener, DefaultLwjglApp}
import com.example.common.bufferF

class AnimClear extends ApplicationListener {
  private val clearC = bufferF(Array(0.0f, 0.25f, 0.0f, 1.0f))

  override def init() { }

  override def update(time: Double) {
    clearC.put(0, sin(time).toFloat*0.5f+0.5f)
    clearC.put(1, cos(time).toFloat*0.5f+0.5f)
  }

  override def render() {
    glClearBufferfv(GL_COLOR, 0, clearC)
  }

  override def dispose() {
    memFree(clearC)
  }
}

object AnimClear extends App {
  val app = new DefaultLwjglApp(new AnimClear()) {
    override val title  = "OpenGL SuperBible - Animated Clear"
    override val width  = 800
    override val height = 600
  }

  app.run()
}
