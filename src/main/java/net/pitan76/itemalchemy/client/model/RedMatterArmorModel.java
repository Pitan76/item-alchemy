package net.pitan76.itemalchemy.client.model;

import net.pitan76.mcpitanlib.api.client.model.CompatCubes;
import net.pitan76.mcpitanlib.api.client.model.CompatModelBuilder;
import net.pitan76.mcpitanlib.api.client.model.CompatModelPart;
import net.pitan76.mcpitanlib.api.client.model.CompatPose;

/**
 * Red Matter Armor model (converted from the Blockbench 4.7.4 export)
 */
public class RedMatterArmorModel {

    public static CompatModelBuilder create() {
        CompatModelBuilder model = CompatModelBuilder.create(128, 128);
        CompatModelPart root = model.root();

        CompatModelPart head = root.addChild("Head", CompatCubes.create()
                .texOffs(0, 15).addBox(-4, -8, -4, 8, 8, 8)
                .texOffs(45, 76).addBox(-4, -7, -5, 8, 3, 2)
                .texOffs(0, 0).addBox(-4.5f, -9, -4.5f, 9, 6, 9),
                CompatPose.offset(0, 0, 0));

        head.addChild("cube_r1", CompatCubes.create()
                .texOffs(0, 0).addBox(-5, -11, -3, 1, 5, 2)
                .texOffs(57, 2).addBox(-3, -12, -5, 1, 9, 2)
                .texOffs(57, 2).addBox(2, -12, -5, 1, 9, 2)
                .texOffs(0, 15).addBox(4, -11, -3, 1, 5, 2),
                CompatPose.offsetAndRotation(0, 0, 0, -0.6545f, 0, 0));

        head.addChild("cube_r2", CompatCubes.create()
                .texOffs(27, 4).addBox(-3, -5, -4, 6, 1, 2),
                CompatPose.offsetAndRotation(0, 0, 0, 0.1745f, 0, 0));

        root.addChild("Body", CompatCubes.create()
                .texOffs(0, 31).addBox(-4, 0, -2, 8, 12, 4)
                .texOffs(28, 7).addBox(-3, 0, -4, 6, 6, 8)
                .texOffs(26, 25).addBox(-4, 0, -3, 8, 11, 6),
                CompatPose.offset(0, 0, 0));

        CompatModelPart rightArm = root.addChild("RightArm", CompatCubes.create()
                .texOffs(32, 54).addBox(-3, -2, -2, 4, 12, 4)
                .texOffs(48, 54).addBox(-3.5f, -2, -2.5f, 5, 6, 5)
                .texOffs(62, 41).addBox(-3.5f, 6, -2.5f, 5, 3, 5),
                CompatPose.offset(-5, 2, 0));

        rightArm.addChild("cube_r3", CompatCubes.create()
                .texOffs(56, 11).addBox(-5.5f, -4.5f, -1.5f, 3.5f, 2.5f, 3)
                .texOffs(43, 42).addBox(-4.5f, -3.5f, -3.5f, 5.5f, 4.5f, 7),
                CompatPose.offsetAndRotation(0, 0, 0, 0, 0, 0.1745f));

        CompatModelPart leftArm = root.addChild("LeftArm", CompatCubes.create()
                .texOffs(54, 17).addBox(-1, -2, -2, 4, 12, 4)
                .texOffs(48, 0).addBox(-1.5f, -2, -2.5f, 5, 6, 5)
                .texOffs(54, 33).addBox(-1.5f, 6, -2.5f, 5, 3, 5),
                CompatPose.offset(5, 2, 0));

        leftArm.addChild("cube_r4", CompatCubes.create()
                .texOffs(36, 42).addBox(1.5f, -4.5f, -1.5f, 3.5f, 2.5f, 3)
                .texOffs(17, 42).addBox(-1.5f, -3.5f, -3.5f, 5.5f, 4.5f, 7),
                CompatPose.offsetAndRotation(0, 0, 0, 0, 0, -0.1745f));

        root.addChild("RightLeg", CompatCubes.create()
                .texOffs(16, 54).addBox(-2, 0, -2, 4, 12, 4)
                .texOffs(40, 21).addBox(-1, 5, -2.5f, 2, 2, 2),
                CompatPose.offset(-1.9f, 12, 0));

        root.addChild("LeftLeg", CompatCubes.create()
                .texOffs(0, 47).addBox(-2, 0, -2, 4, 12, 4)
                .texOffs(32, 21).addBox(-1, 5, -2.5f, 2, 2, 2),
                CompatPose.offset(1.9f, 12, 0));

        return model;
    }
}
