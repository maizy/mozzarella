package space.maizy.mozzarella.minetest_proto.data

/**
 * Copyright (c) Nikita Kovaliov, maizy.space, 2019
 * See LICENSE.txt for details.
 */

import scala.language.implicitConversions
import scodec.bits.ByteVector

/**
 * generated from f
 */
object ToServerCommand extends Enumeration {

  type Type = Value

  protected case class Val(asInt: Int) extends super.Val {
    val asByte: ByteVector = ByteVector(asInt)
    override def toString(): String = super.toString().replace("TOSERVER_", "")
  }

  implicit def valueToVal(x: Value): Val = x.asInstanceOf[Val]

  // scalastyle:off indentation
  val TOSERVER_EMPTY = Val(0x00)

  val TOSERVER_INIT = Val(0x02)
  /*
      Sent first after connected.
      u8 serialisation version (=SER_FMT_VER_HIGHEST_READ)
      u16 supported network compression modes
      u16 minimum supported network protocol version
      u16 maximum supported network protocol version
      std::string player name
  */

  val TOSERVER_INIT_LEGACY = Val(0x10) // Obsolete

  val TOSERVER_INIT2 = Val(0x11)
  /*
      Sent as an ACK for TOCLIENT_INIT.
      After this, the server can send data.
      [0] u16 val TOSERVER_INIT2
  */

  val TOSERVER_MODCHANNEL_JOIN = Val(0x17)
  /*
      u16 channel name length
      std::string channel name
   */

  val TOSERVER_MODCHANNEL_LEAVE = Val(0x18)
  /*
      u16 channel name length
      std::string channel name
   */

  val TOSERVER_MODCHANNEL_MSG = Val(0x19)
  /*
      u16 channel name length
      std::string channel name
      u16 message length
      std::string message
   */

  val TOSERVER_GETBLOCK = Val(0x20) // Obsolete
  val TOSERVER_ADDNODE = Val(0x21) // Obsolete
  val TOSERVER_REMOVENODE = Val(0x22) // Obsolete

  val TOSERVER_PLAYERPOS = Val(0x23)
  /*
      [0] u16 command
      [2] v3s32 position*100
      [2+12] v3s32 speed*100
      [2+12+12] s32 pitch*100
      [2+12+12+4] s32 yaw*100
      [2+12+12+4+4] u32 keyPressed
      [2+12+12+4+4+1] u8 fov*80
      [2+12+12+4+4+4+1] u8 ceil(wanted_range / MAP_BLOCKSIZE)
  */

  val TOSERVER_GOTBLOCKS = Val(0x24)
  /*
      [0] u16 command
      [2] u8 count
      [3] v3s16 pos_0
      [3+6] v3s16 pos_1
      ...
  */

  val TOSERVER_DELETEDBLOCKS = Val(0x25)
  /*
      [0] u16 command
      [2] u8 count
      [3] v3s16 pos_0
      [3+6] v3s16 pos_1
      ...
  */

  val TOSERVER_ADDNODE_FROM_INVENTORY = Val(0x26) // Obsolete
  val TOSERVER_CLICK_OBJECT = Val(0x27) // Obsolete
  val TOSERVER_GROUND_ACTION = Val(0x28) // Obsolete
  val TOSERVER_RELEASE = Val(0x29) // Obsolete
  val TOSERVER_SIGNTEXT = Val(0x30) // Obsolete

  val TOSERVER_INVENTORY_ACTION = Val(0x31)
  /*
      See InventoryAction in inventorymanager.h
  */

  val TOSERVER_CHAT_MESSAGE = Val(0x32)
  /*
      u16 length
      wstring message
  */

  val TOSERVER_SIGNNODETEXT = Val(0x33) // Obsolete
  val TOSERVER_CLICK_ACTIVEOBJECT = Val(0x34) // Obsolete

  val TOSERVER_DAMAGE = Val(0x35)
  /*
      u8 amount
  */

  val TOSERVER_PASSWORD_LEGACY = Val(0x36) // Obsolete

  val TOSERVER_PLAYERITEM = Val(0x37)
  /*
      Sent to change selected item.
      [0] u16 val TOSERVER_PLAYERITEM
      [2] u16 item
  */

  val TOSERVER_RESPAWN = Val(0x38)
  /*
      u16 val TOSERVER_RESPAWN
  */

  val TOSERVER_INTERACT = Val(0x39)
  /*
      [0] u16 command
      [2] u8 action
      [3] u16 item
      [5] u32 length of the next item
      [9] serialized PointedThing
      actions:
      0: start digging (from undersurface) or use
      1: stop digging (all parameters ignored)
      2: digging completed
      3: place block or item (to abovesurface)
      4: use item
  */

  val TOSERVER_REMOVED_SOUNDS = Val(0x3a)
  /*
      u16 len
      s32[len] sound_id
  */

  val TOSERVER_NODEMETA_FIELDS = Val(0x3b)
  /*
      v3s16 p
      u16 len
      u8[len] form name (reserved for future use)
      u16 number of fields
      for each field:
          u16 len
          u8[len] field name
          u32 len
          u8[len] field value
  */

  val TOSERVER_INVENTORY_FIELDS = Val(0x3c)
  /*
      u16 len
      u8[len] form name (reserved for future use)
      u16 number of fields
      for each field:
          u16 len
          u8[len] field name
          u32 len
          u8[len] field value
  */

  val TOSERVER_REQUEST_MEDIA = Val(0x40)
  /*
      u16 number of files requested
      for each file {
          u16 length of name
          string name
      }
  */

  val TOSERVER_RECEIVED_MEDIA = Val(0x41) // Obsolete
  val TOSERVER_BREATH = Val(0x42) // Obsolete

  val TOSERVER_CLIENT_READY = Val(0x43)
  /*
      u8 major
      u8 minor
      u8 patch
      u8 reserved
      u16 len
      u8[len] full_version_string
  */

  val TOSERVER_FIRST_SRP = Val(0x50)
  /*
      Belonging to AUTH_MECHANISM_FIRST_SRP.
      std::string srp salt
      std::string srp verification key
      u8 is_empty (=1 if password is empty, 0 otherwise)
  */

  val TOSERVER_SRP_BYTES_A = Val(0x51)
  /*
      Belonging to AUTH_MECHANISM_SRP,
          depending on current_login_based_on.
      std::string bytes_A
      u8 current_login_based_on : on which version of the password's
                                  hash this login is based on (0 legacy hash,
                                  or 1 directly the password)
  */

  val TOSERVER_SRP_BYTES_M = Val(0x52)
  /*
      Belonging to AUTH_MECHANISM_SRP.
      std::string bytes_M
  */

  val TOSERVER_NUM_MSG_TYPES = Val(0x53)


  val index: Map[Int, ToServerCommand.Type] = values.map(v => v.asInt -> v).toMap
  val byteIndex: Map[ByteVector, ToServerCommand.Type] = values.map(v => v.asByte -> v).toMap
}
