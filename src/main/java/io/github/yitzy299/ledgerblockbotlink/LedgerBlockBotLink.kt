package io.github.yitzy299.ledgerblockbotlink

import com.github.quiltservertools.ledger.api.ExtensionManager.registerExtension
import com.github.quiltservertools.ledger.database.DatabaseManager
import io.github.quiltservertools.blockbotapi.BlockBotApi
import io.github.quiltservertools.blockbotapi.Channels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader

object LedgerBlockBotLink : ModInitializer, CoroutineScope {

    lateinit var CONFIG: Config

    override fun onInitialize() {
        CONFIG = Config().readConfig(FabricLoader.getInstance().configDir.resolve("ledgerblockbotlink.json"))
        CONFIG.listeners.forEach {
            it.actionIdentifiers.forEach { action ->
                registerMessageSend(action, it.block)
            }
        }
    }

    private fun registerMessageSend(block: String, event: String) {
        launch {
            DatabaseManager.actions.collect {
                if ((it.identifier == event || event == "*") && (block == it.objectIdentifier.toString() || block == it.oldObjectIdentifier.toString())) {
                    BlockBotApi.sendRelayMessage("${if (it.sourceProfile != null) {
                        it.sourceProfile!!.name
                    } else {
                        it.sourceName
                    }
                    } performed action ${it.identifier} on ${
                        if (it.blockState == null) {
                            it.oldBlockState!!.block.name.string
                        } else {
                            it.blockState!!.block.name.string
                        }
                    }", Channels.ALERT)
                }
            }
        }
    }

    override val coroutineContext = Dispatchers.IO
}