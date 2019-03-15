package com.jeremyliao.atg.generator.type;

import com.squareup.javapoet.TypeSpec;

/**
 * Created by liaohailiang on 2019/3/13.
 */
public interface ITypeGenerator {

    TypeSpec.Builder getTypeSpecBuilder(String testClassName);
}
