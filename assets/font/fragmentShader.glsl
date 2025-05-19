 #version 330 core
 in vec2 texCoord;
 out vec4 FragColor;
 uniform sampler2D tex;
 uniform vec4 color;

 void main() {
     float alpha = texture(tex, texCoord).r;
     FragColor = vec4(color.rgb, color.a * alpha);
 }