package com.stormbreaker.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.stormbreaker.config.StormbreakerConfig;
import com.stormbreaker.entity.StormbreakerProjectileEntity;
import com.stormbreaker.registry.ModRarities;
import com.stormbreaker.util.StormbreakerAbilities;
import com.stormbreaker.util.StormbreakerTier;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StormbreakerItem extends SwordItem implements GeoItem {
    private static final double DEFAULT_BASE_DAMAGE = 22.0D;
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("6f91fd38-ced3-42c8-b1fd-6469dcf46f03");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("3b67f975-3174-4dee-a1e8-b41904c8f76d");
    private static final UUID ATTACK_KNOCKBACK_UUID = UUID.fromString("b8b5df44-2ccb-45bc-a65f-f9aa607a50e6");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.stormbreaker.idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Multimap<Attribute, AttributeModifier> attributes;

    public StormbreakerItem() {
        super(StormbreakerTier.STORMBREAKER, 4, -2.8F,
                new Item.Properties()
                        .stacksTo(1)
                        .durability(4500)
                        .rarity(ModRarities.STORMBREAKER)
                        .fireResistant());
        this.attributes = ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_UUID, "Weapon modifier", DEFAULT_BASE_DAMAGE, AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_UUID, "Weapon modifier", -2.8D, AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ATTACK_KNOCKBACK_UUID, "Weapon modifier", 1.25D, AttributeModifier.Operation.ADDITION))
                .build();
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? attributes : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(held, true);
        }

        if (player.isCrouching()) {
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResultHolder.fail(held);
            }
            if (!level.isClientSide) {
                ItemStack thrown = held.copyWithCount(1);
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                StormbreakerProjectileEntity entity = new StormbreakerProjectileEntity(level, player, thrown);
                level.addFreshEntity(entity);
                player.getCooldowns().addCooldown(this, StormbreakerConfig.THROW_COOLDOWN_TICKS.get());
                level.playSound(null, player.blockPosition(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 0.9F);
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(held);
        }
        StormbreakerAbilities.castLightning(serverPlayer, hand);
        return InteractionResultHolder.success(held);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, net.minecraft.world.entity.LivingEntity target, net.minecraft.world.entity.LivingEntity attacker) {
        boolean hurt = super.hurtEnemy(stack, target, attacker);
        if (attacker.level().isThundering()) {
            target.setSecondsOnFire(2);
        }
        return hurt;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltips, TooltipFlag flag) {
        tooltips.add(Component.translatable("tooltip.stormbreaker.legendary"));
        tooltips.add(Component.translatable("tooltip.stormbreaker.lightning"));
        tooltips.add(Component.translatable("tooltip.stormbreaker.throw"));
        tooltips.add(Component.translatable("tooltip.stormbreaker.thunder"));
        tooltips.add(Component.translatable("tooltip.stormbreaker.bifrost"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "stormbreaker_idle", 0, state -> state.setAndContinue(IDLE_ANIM)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final Supplier<BlockEntityWithoutLevelRenderer> renderer = () -> new com.stormbreaker.renderer.StormbreakerItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer.get();
            }
        });
    }
}
