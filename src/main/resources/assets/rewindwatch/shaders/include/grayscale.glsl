#version 150

vec4 grayscale(vec4 color) {
    float brightness = color.r * 0.2126 + color.g * 0.7152 + color.b * 0.0722;
    return vec4(brightness, brightness, brightness, color.a);
}
