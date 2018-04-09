package com.hack.kind.lvivbikemap.data

import android.content.Context
import androidx.core.content.edit

fun putCategoryChecked(ctx: Context, key: String, value: Boolean) =
        ctx.getSharedPreferences(ctx.packageName, Context.MODE_PRIVATE).edit { putBoolean(key, value) }

fun categoryChecked(ctx: Context, key: String) =
        ctx.getSharedPreferences(ctx.packageName, Context.MODE_PRIVATE).getBoolean(key, true)


