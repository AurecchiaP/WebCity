clc
clear all
close all

x = [100 250 500 1000 2000];
y1 = [59 95 187 320 560]; % 720p no orbit
y2 = [58 130 264 494 835]; % 720p orbit
y3 = [62 162 285 515 916]; % 1080p no orbit
y4 = [88 216 561 834 1646]; % 1080p orbit
y5 = [89 206 421 730 1516]; % 1440p no orbit
y6 = [132 324 657 1194 2398]; % 1440p orbit

figure
plot(x,y1, '-o');
hold on;
plot(x,y2, '-o');
hold on;
plot(x,y3, '-o');
hold on;
plot(x,y4, '-o');
hold on;
plot(x,y5, '-o');
hold on;
plot(x,y6, '-o');
legend('720p','720p orbit','1080p','1080p orbit','1440p','1440p orbit')
xlabel('#Versions')
ylabel('Seconds')
% title('plot 1')
print('-dpng','-r0')
