package space.maizy.mozzarella.minetest_proto.data

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scala.language.implicitConversions
import scodec.bits.ByteVector

/**
 * generated from https://github.com/minetest/minetest/blob/5.0.1/src/network/networkprotocol.h
 */
object ToClientCommand extends Enumeration {

  type Type = Value

  protected case class Val(asInt: Int) extends super.Val {
    val asByte: ByteVector = ByteVector(asInt)

    override def toString(): String = super.toString().replace("TOCLIENT_", "")
  }

  implicit def valueToVal(x: Value): Val = x.asInstanceOf[Val]

  // scalastyle:off indentation
  val TOCLIENT_HELLO = Val(0x02)
  /*
      Sent after TOSERVER_INIT.
      u8 deployed serialisation version
      u16 deployed network compression mode
      u16 deployed protocol version
      u32 supported auth methods
      std::string username that should be used for legacy hash (for proper casing)
  */
  val TOCLIENT_AUTH_ACCEPT = Val(0x03)
  /*
      Message from server to accept auth.
      v3s16 player's position + v3f(0,BS/2,0) floatToInt'd
      u64 map seed
      f1000 recommended send interval
      u32 : supported auth methods for sudo mode
            (where the user can change their password)
  */
  val TOCLIENT_ACCEPT_SUDO_MODE = Val(0x04)
  /*
      Sent to client to show it is in sudo mode now.
  */
  val TOCLIENT_DENY_SUDO_MODE = Val(0x05)
  /*
      Signals client that sudo mode auth failed.
  */
  val TOCLIENT_ACCESS_DENIED = Val(0x0A)
  /*
      u8 reason
      std::string custom reason (if needed, otherwise "")
      u8 (bool) reconnect
  */

  val TOCLIENT_INIT_LEGACY = Val(0x10) // Obsolete

  val TOCLIENT_BLOCKDATA = Val(0x20) // TODO: Multiple blocks

  val TOCLIENT_ADDNODE = Val(0x21)
  /*
      v3s16 position
      serialized mapnode
      u8 keep_metadata // Added in protocol version 22
  */
  val TOCLIENT_REMOVENODE = Val(0x22)

  val TOCLIENT_PLAYERPOS = Val(0x23) // Obsolete
  val TOCLIENT_PLAYERINFO = Val(0x24) // Obsolete
  val TOCLIENT_OPT_BLOCK_NOT_FOUND = Val(0x25) // Obsolete
  val TOCLIENT_SECTORMETA = Val(0x26) // Obsolete

  val TOCLIENT_INVENTORY = Val(0x27)
  /*
      [0] u16 command
      [2] serialized inventory
  */

  val TOCLIENT_OBJECTDATA = Val(0x28) // Obsolete

  val TOCLIENT_TIME_OF_DAY = Val(0x29)
  /*
      u16 time (0-23999)
      Added in a later version:
      f1000 time_speed
  */

  val TOCLIENT_CSM_RESTRICTION_FLAGS = Val(0x2A)
  /*
      u32 CSMRestrictionFlags byteflag
   */

  // (oops, there is some gap here)

  val TOCLIENT_CHAT_MESSAGE = Val(0x2F)
  /*
      u8 version
      u8 message_type
      u16 sendername length
      wstring sendername
      u16 length
      wstring message
  */

  val TOCLIENT_CHAT_MESSAGE_OLD = Val(0x30) // Obsolete

  val TOCLIENT_ACTIVE_OBJECT_REMOVE_ADD = Val(0x31)
  /*
      u16 count of removed objects
      for all removed objects {
          u16 id
      }
      u16 count of added objects
      for all added objects {
          u16 id
          u8 type
          u32 initialization data length
          string initialization data
      }
  */

  val TOCLIENT_ACTIVE_OBJECT_MESSAGES = Val(0x32)
  /*
      for all objects
      {
          u16 id
          u16 message length
          string message
      }
  */

  val TOCLIENT_HP = Val(0x33)
  /*
      u8 hp
  */

  val TOCLIENT_MOVE_PLAYER = Val(0x34)
  /*
      v3f1000 player position
      f1000 player pitch
      f1000 player yaw
  */

  val TOCLIENT_ACCESS_DENIED_LEGACY = Val(0x35)
  /*
      u16 reason_length
      wstring reason
  */

  val TOCLIENT_PLAYERITEM = Val(0x36) // Obsolete

  val TOCLIENT_DEATHSCREEN = Val(0x37)
  /*
      u8 bool set camera point target
      v3f1000 camera point target (to point the death cause or whatever)
  */

  val TOCLIENT_MEDIA = Val(0x38)
  /*
      u16 total number of texture bunches
      u16 index of this bunch
      u32 number of files in this bunch
      for each file {
          u16 length of name
          string name
          u32 length of data
          data
      }
      u16 length of remote media server url (if applicable)
      string url
  */

  val TOCLIENT_TOOLDEF = Val(0x39)
  /*
      u32 length of the next item
      serialized ToolDefManager
  */

  val TOCLIENT_NODEDEF = Val(0x3a)
  /*
      u32 length of the next item
      serialized NodeDefManager
  */

  val TOCLIENT_CRAFTITEMDEF = Val(0x3b)
  /*
      u32 length of the next item
      serialized CraftiItemDefManager
  */

  val TOCLIENT_ANNOUNCE_MEDIA = Val(0x3c)

  /*
      u32 number of files
      for each texture {
          u16 length of name
          string name
          u16 length of sha1_digest
          string sha1_digest
      }
  */

  val TOCLIENT_ITEMDEF = Val(0x3d)
  /*
      u32 length of next item
      serialized ItemDefManager
  */

  val TOCLIENT_PLAY_SOUND = Val(0x3f)
  /*
      s32 sound_id
      u16 len
      u8[len] sound name
      s32 gain*1000
      u8 type (0=local, 1=positional, 2=object)
      s32[3] pos_nodes*10000
      u16 object_id
      u8 loop (bool)
  */

  val TOCLIENT_STOP_SOUND = Val(0x40)
  /*
      s32 sound_id
  */

  val TOCLIENT_PRIVILEGES = Val(0x41)
  /*
      u16 number of privileges
      for each privilege
          u16 len
          u8[len] privilege
  */

  val TOCLIENT_INVENTORY_FORMSPEC = Val(0x42)
  /*
      u32 len
      u8[len] formspec
  */

  val TOCLIENT_DETACHED_INVENTORY = Val(0x43)
  /*
      [0] u16 command
      u16 len
      u8[len] name
      [2] serialized inventory
  */

  val TOCLIENT_SHOW_FORMSPEC = Val(0x44)
  /*
      [0] u16 command
      u32 len
      u8[len] formspec
      u16 len
      u8[len] formname
  */

  val TOCLIENT_MOVEMENT = Val(0x45)
  /*
      f1000 movement_acceleration_default
      f1000 movement_acceleration_air
      f1000 movement_acceleration_fast
      f1000 movement_speed_walk
      f1000 movement_speed_crouch
      f1000 movement_speed_fast
      f1000 movement_speed_climb
      f1000 movement_speed_jump
      f1000 movement_liquid_fluidity
      f1000 movement_liquid_fluidity_smooth
      f1000 movement_liquid_sink
      f1000 movement_gravity
  */

  val TOCLIENT_SPAWN_PARTICLE = Val(0x46)
  /*
      v3f1000 pos
      v3f1000 velocity
      v3f1000 acceleration
      f1000 expirationtime
      f1000 size
      u8 bool collisiondetection
      u32 len
      u8[len] texture
      u8 bool vertical
      u8 collision_removal
      TileAnimation animation
      u8 glow
      u8 object_collision
  */

  val TOCLIENT_ADD_PARTICLESPAWNER = Val(0x47)
  /*
      u16 amount
      f1000 spawntime
      v3f1000 minpos
      v3f1000 maxpos
      v3f1000 minvel
      v3f1000 maxvel
      v3f1000 minacc
      v3f1000 maxacc
      f1000 minexptime
      f1000 maxexptime
      f1000 minsize
      f1000 maxsize
      u8 bool collisiondetection
      u32 len
      u8[len] texture
      u8 bool vertical
      u8 collision_removal
      u32 id
      TileAnimation animation
      u8 glow
      u8 object_collision
  */

  val TOCLIENT_DELETE_PARTICLESPAWNER_LEGACY = Val(0x48) // Obsolete

  val TOCLIENT_HUDADD = Val(0x49)
  /*
      u32 id
      u8 type
      v2f1000 pos
      u32 len
      u8[len] name
      v2f1000 scale
      u32 len2
      u8[len2] text
      u32 number
      u32 item
      u32 dir
      v2f1000 align
      v2f1000 offset
      v3f1000 world_pos
      v2s32 size
  */

  val TOCLIENT_HUDRM = Val(0x4a)
  /*
      u32 id
  */

  val TOCLIENT_HUDCHANGE = Val(0x4b)
  /*
      u32 id
      u8 stat
      [v2f1000 data |
       u32 len
       u8[len] data |
       u32 data]
  */

  val TOCLIENT_HUD_SET_FLAGS = Val(0x4c)
  /*
      u32 flags
      u32 mask
  */

  val TOCLIENT_HUD_SET_PARAM = Val(0x4d)
  /*
      u16 param
      u16 len
      u8[len] value
  */

  val TOCLIENT_BREATH = Val(0x4e)
  /*
      u16 breath
  */

  val TOCLIENT_SET_SKY = Val(0x4f)
  /*
      u8[4] color (ARGB)
      u8 len
      u8[len] type
      u16 count
      foreach count:
          u8 len
          u8[len] param
      u8 clouds (boolean)
  */

  val TOCLIENT_OVERRIDE_DAY_NIGHT_RATIO = Val(0x50)
  /*
      u8 do_override (boolean)
      u16 day-night ratio 0...65535
  */

  val TOCLIENT_LOCAL_PLAYER_ANIMATIONS = Val(0x51)
  /*
      v2s32 stand/idle
      v2s32 walk
      v2s32 dig
      v2s32 walk+dig
      f1000 frame_speed
  */

  val TOCLIENT_EYE_OFFSET = Val(0x52)
  /*
      v3f1000 first
      v3f1000 third
  */

  val TOCLIENT_DELETE_PARTICLESPAWNER = Val(0x53)
  /*
      u32 id
  */

  val TOCLIENT_CLOUD_PARAMS = Val(0x54)
  /*
      f1000 density
      u8[4] color_diffuse (ARGB)
      u8[4] color_ambient (ARGB)
      f1000 height
      f1000 thickness
      v2f1000 speed
  */

  val TOCLIENT_FADE_SOUND = Val(0x55)
  /*
      s32 sound_id
      float step
      float gain
  */
  val TOCLIENT_UPDATE_PLAYER_LIST = Val(0x56)
  /*
      u8 type
      u16 number of players
      for each player
          u16 len
          u8[len] player name
  */

  val TOCLIENT_MODCHANNEL_MSG = Val(0x57)
  /*
      u16 channel name length
      std::string channel name
      u16 channel name sender
      std::string channel name
      u16 message length
      std::string message
  */

  val TOCLIENT_MODCHANNEL_SIGNAL = Val(0x58)
  /*
      u8 signal id
      u16 channel name length
      std::string channel name
  */

  val TOCLIENT_NODEMETA_CHANGED = Val(0x59)
  /*
      serialized and compressed node metadata
  */

  val TOCLIENT_SRP_BYTES_S_B = Val(0x60)
  /*
      Belonging to AUTH_MECHANISM_SRP.
      std::string bytes_s
      std::string bytes_B
  */

  val TOCLIENT_FORMSPEC_PREPEND = Val(0x61)
  /*
      u16 len
      u8[len] formspec
  */

  val TOCLIENT_NUM_MSG_TYPES = Val(0x62)



  val index: Map[Int, ToClientCommand.Type] = values.map(v => v.asInt -> v).toMap
  val byteIndex: Map[ByteVector, ToClientCommand.Type] = values.map(v => v.asByte -> v).toMap
}
