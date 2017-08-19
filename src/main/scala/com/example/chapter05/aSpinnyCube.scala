/*******************************************************************************
  * Copyright 2015 Serf Productions, LLC.
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

package com.example.chapter05

import org.joml.Math.{sin, cos, toRadians}
import org.joml.Matrix4f
import org.lwjgl.opengl._, GL11._, GL15._, GL20._, GL30._, GL45.glCreateVertexArrays
import org.lwjgl.system.{MemoryUtil, MemoryStack}, MemoryUtil.memFree

import com.example.common._

class aSpinnyCube extends ApplicationListener {
  import aSpinnyCubeShaders._

  private val clr = Array(0.0f, 0.25f, 0.0f, 1.0f)
  private val one = Array(1.0f)

  private lazy val program = loadProgram(vsSrc, fsSrc)

  private lazy val proj_matrix = MemoryUtil memAllocFloat 16
  private lazy val   mv_matrix = MemoryUtil memAllocFloat 16

  private lazy val proj_location =
    glGetUniformLocation(program, "proj_matrix")

  private lazy val mv_location =
    glGetUniformLocation(program, "mv_matrix")

  /** Shared Matrix Object */
  private lazy val m4 = new Matrix4f()

  private lazy val (vao, cubeVbo) =
    tryWith(MemoryStack.stackPush()) { stack =>
      val vaos = stack mallocInt 1
      glCreateVertexArrays(vaos)

      val buffers = stack mallocInt 1
      glGenBuffers(buffers)

      (vaos get 0, buffers get 0)
    }.get

  override def init() {
    glBindVertexArray(vao)

    glBindBuffer(GL_ARRAY_BUFFER, cubeVbo)
    glBufferData(GL_ARRAY_BUFFER, vertex_positions, GL_STATIC_DRAW)

    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0l)
    glEnableVertexAttribArray(0)

    glEnable(GL_CULL_FACE)
    glFrontFace(GL_CW)

    glEnable(GL_DEPTH_TEST)
    glDepthFunc(GL_LEQUAL)

    glViewport(0, 0, 800, 600)
    initPerspective()
  }

  override def update(time: Double): Unit = {
    val f = time*0.3

    m4.translation(0.0f, 0.0f, -4.0f)
      .translation (
        (sin(2.1*f)*0.5).asInstanceOf[Float],
        (cos(1.7*f)*0.5).asInstanceOf[Float],
        (sin(1.3*f)*cos(1.5*f)*2.0-4.0).asInstanceOf[Float] )
      .rotate(toRadians(time*45.0).asInstanceOf[Float], 0.0f, 1.0f, 0.0f)
      .rotate(toRadians(time*81.0).asInstanceOf[Float], 1.0f, 0.0f, 0.0f)
      .get(mv_matrix)
  }

  override def render() {
    glClearBufferfv(GL_COLOR, 0, clr)
    glClearBufferfv(GL_DEPTH, 0, one)

    glUseProgram(program)
    glUniformMatrix4fv(proj_location, false, proj_matrix)
    glUniformMatrix4fv(  mv_location, false, mv_matrix)
    glDrawArrays(GL_TRIANGLES, 0, 36)
  }

  override def dispose() {
    glDeleteVertexArrays(vao)
    glDeleteBuffers(cubeVbo)
    glDeleteProgram(program)

    memFree(proj_matrix)
    memFree(mv_matrix)
  }

  private def initPerspective() {
    val fovy   = toRadians(50.0d).asInstanceOf[Float]
    val aspect = 800f/600f
    val znear  =    0.1f
    val zfar   = 1000.0f

    m4.setPerspective(fovy, aspect, znear, zfar)
      .get(proj_matrix)
  }
}

object aSpinnyCube extends App {
  val app = new DefaultLwjglApp(new aSpinnyCube()) {
    override val title  = "OpenGL SuperBible - Spinny Cube"
    override val width  = 800
    override val height = 600
  }

  app.run()
}

object aSpinnyCubeShaders {
  val vsSrc: (Int, String) =
    GL_VERTEX_SHADER ->
      """#version 410 core
        |
        |in vec4 position;
        |
        |out VS_OUT {
        |  vec4 color;
        |} vs_out;
        |
        |uniform mat4 mv_matrix;
        |uniform mat4 proj_matrix;
        |
        |void main(void) {
        |  gl_Position = proj_matrix * mv_matrix * position;
        |  vs_out.color = position * 2.0 + vec4(0.5, 0.5, 0.5, 0.0);
        |}""".stripMargin

  val fsSrc: (Int, String) =
    GL_FRAGMENT_SHADER ->
      """#version 410 core
        |
        |out vec4 color;
        |
        |in VS_OUT {
        |  vec4 color;
        |} fs_in;
        |
        |void main(void) {
        |  color = fs_in.color;
        |}""".stripMargin

  val vertex_positions: Array[Float] =
    Array (
      -0.25f,  0.25f, -0.25f,
      -0.25f, -0.25f, -0.25f,
       0.25f, -0.25f, -0.25f,

       0.25f, -0.25f, -0.25f,
       0.25f,  0.25f, -0.25f,
      -0.25f,  0.25f, -0.25f,

       0.25f, -0.25f, -0.25f,
       0.25f, -0.25f,  0.25f,
       0.25f,  0.25f, -0.25f,

       0.25f, -0.25f,  0.25f,
       0.25f,  0.25f,  0.25f,
       0.25f,  0.25f, -0.25f,

       0.25f, -0.25f,  0.25f,
      -0.25f, -0.25f,  0.25f,
       0.25f,  0.25f,  0.25f,

      -0.25f, -0.25f,  0.25f,
      -0.25f,  0.25f,  0.25f,
       0.25f,  0.25f,  0.25f,

      -0.25f, -0.25f,  0.25f,
      -0.25f, -0.25f, -0.25f,
      -0.25f,  0.25f,  0.25f,

      -0.25f, -0.25f, -0.25f,
      -0.25f,  0.25f, -0.25f,
      -0.25f,  0.25f,  0.25f,

      -0.25f, -0.25f,  0.25f,
       0.25f, -0.25f,  0.25f,
       0.25f, -0.25f, -0.25f,

       0.25f, -0.25f, -0.25f,
      -0.25f, -0.25f, -0.25f,
      -0.25f, -0.25f,  0.25f,

      -0.25f,  0.25f, -0.25f,
       0.25f,  0.25f, -0.25f,
       0.25f,  0.25f,  0.25f,

       0.25f,  0.25f,  0.25f,
      -0.25f,  0.25f,  0.25f,
      -0.25f,  0.25f, -0.25f )
}
