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

package com.example.common

trait ApplicationListener {
  /** Pre render-loop initialization and setup occurs here. */
  def init(): Unit = {}

  /** Update the application's current state. */
  def update(time: Double): Unit = {}

  /** Renders the application's current state. */
  def render(): Unit = {}

  /** Post render-loop, cleanup instructions. */
  def dispose(): Unit = {}

  /**
   * Hook for handling re-size events.
   *
   * @param width  the window's new width  in pixels
   * @param height the window's new height in pixels
   */
  def resize(width: Int, height: Int): Unit = {}
}
