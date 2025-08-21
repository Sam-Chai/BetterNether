package org.betterx.betternether.mixin.common.piglin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.betterx.betternether.config.Configs;
import org.betterx.betternether.items.materials.BNArmorTiers;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ArmorMaterial;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.logging.Logger;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
    // WHAT THE FUCK THE CONNECTOR DIDN'T SUPPORT THIS SHITTY CODE

//    @WrapOperation(
//            method = "isWearingGold",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/core/Holder;)Z"
//            )
//    )
//    private static boolean bn_isWearingGold(
//            Holder<ArmorMaterial> instance,
//            Holder<ArmorMaterial> tHolder,
//            Operation<Boolean> original
//    ) {
//        //Piglins will now also consider BetterNether armor materials as gold armor
//        return original.call(instance, tHolder) ||
//                (Configs.GAME_RULES.piglinIgnoreNetherArmor.get() && (instance.is(BNArmorTiers.CINCINNASITE.armorMaterial) || instance.is(BNArmorTiers.NETHER_RUBY.armorMaterial) || instance.is(BNArmorTiers.FLAMING_RUBY.armorMaterial)));
//    }

    @Inject(method = "isWearingGold(Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("RETURN"), cancellable = true)
    private static void alwaysTreatBNAsGold(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) return;
        if (!(entity instanceof Player player)) return;

        try {
            for (ItemStack stack : player.getArmorSlots()) {
                if (stack.isEmpty()) continue;
                if (stack.getItem() instanceof ArmorItem armor) {
                    Holder<ArmorMaterial> mat = armor.getMaterial();
                    if (mat.is(BNArmorTiers.CINCINNASITE.armorMaterial)
                            || mat.is(BNArmorTiers.NETHER_RUBY.armorMaterial)
                            || mat.is(BNArmorTiers.FLAMING_RUBY.armorMaterial)) {
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
        } catch (Throwable ignored) {
            // BN 缺失或字段变动时静默忽略，保证不崩
        }
    }
}
