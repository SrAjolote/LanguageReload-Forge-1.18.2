/*
 * Decompiled with CFR 0.152.
 */
package org.hiedacamellia.languagereload.core.mixin;

import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={SignBlockEntity.class})
public interface SignTextAccessor {
    @Accessor(value="renderMessages")
    public void languagereload_setRenderMessages(FormattedCharSequence[] var1);
}
