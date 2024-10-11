#moj_import <rewindwatch:lygia/generative/cnoise.glsl>

bool shouldDissolve(vec2 coord, float opacity) {
    return cnoise(coord * 16) + 1 > opacity * 2;
}
