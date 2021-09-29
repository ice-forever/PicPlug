package vcg.Config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value
import vcg.Utils.Resources

object Config :AutoSavePluginConfig("${Resources.NAME}Config"){
    var imageAPI: String by value(Resources.IMG_API_URL)
    var groupList : List<Long> by value(arrayListOf<Long>(1234567890L, 9876543210L))
    var commandPrefix : String by value(Resources.COMMAND_PREFIX)
    var imageStorage : String by value(Resources.IMG_STORAGE_PATH)
}