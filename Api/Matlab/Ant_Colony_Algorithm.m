%%原始蚁周系统的蚁群算法
%%清空环境变量
clear all; %清除所有变量
close all; %清图
clc ;      %清屏

%%初始化参数----------------------------------------------------------
m=50;       %% m 蚂蚁个数
Alpha=1;    %% Alpha 信息素重要程度因子
Beta=5;     %% Beta 启发函数重要程度因子
Rho=0.1;    %% Rho 信息素挥发系数
NC_max=200; %%最大迭代次数
Q=100;      %%蚂蚁循环一次释放的信息素总量

Coordinate=[
    
117.24472	30.702345;
117.246349	30.715923;
117.504271	30.896929;
117.242798	30.705608;
117.24404	30.702824;
117.245211	30.703753;
117.232213	30.706042;
117.231326	30.702665;
117.230748	30.700744;
117.221733	30.703528;
117.2218686	30.70332001;
117.230085	30.697414;
117.219799	30.70411;
117.219715	30.701481;
117.25443	30.699017;
117.247473	30.714931;


    ];
%坐标矩阵

%%计算结点间的相互距离，初始化数组-------------------------------------------
n = size(Coordinate,1);
D = zeros(n,n);%创建赋路径权值的矩阵

%%计算路径距离并赋值--------------------------------------------------------
for i=1:n
    for j=1:n
        if i~=j
            D(i,j)=((Coordinate(i,1)-Coordinate(j,1))^2+(Coordinate(i,2)-Coordinate(j,2))^2)^0.5;
        else
            D(i,j)=eps;      %i=j时不计算，由于后面的启发因子要取倒数，用eps（浮点相对精度）表示
        end
        D(j,i)=D(i,j);   %对称矩阵
    end
end
%%设置与路径长度有关的信息--------------------------------------------------
Eta=1./D;          %Eta为启发函数，这里设为距离的倒数
Tau=ones(n,n);     %Tau为信息素矩阵
Tabu=zeros(m,n);   %存储并记录路径的生成，禁忌表
NC=1;               %迭代计数器，记录迭代次数
Route_Best=zeros(NC_max,n);       %每代最佳路线
Length_Best=inf.*ones(NC_max,1);   %每代最佳路线的长度
L_ave=zeros(NC_max,1);        %每代路线的平均长度

%%核心算法部分------------------------------------------------------------
while NC<=NC_max        %停止条件之一，达到最大迭代次数则停止
    %%将m只蚂蚁随机放到n个结点上
    Randpos=[];   %随即存取，用于存放每只蚂蚁初始结点位置
    
    %%确定m只蚂蚁与结点的倍数，每次将n只蚂蚁均放到n个结点，直至m只蚂蚁全部放入结点中
    for i=1:(ceil(m/n)) %向上取整
        Randpos=[Randpos,randperm(n)];
        %randperm(n)产生1到n的整数的无重复的随机排列填充，生成一个二维数组
    end
    Tabu(:,1)=(Randpos(1,1:m))';   %将蚂蚁所在结点存放在第一列
    
    %%m只蚂蚁按概率函数选择下一结点
    for j=2:n     %剩余结点数，蚂蚁自身所在的结点不计算
        for i=1:m
            visited=Tabu(i,1:(j-1)); %记录已访问过的结点
            Wait_point=zeros(1,(n-j+1));       %当前蚂蚁待访问的结点
            P=Wait_point;                      %设置待访问结点的选择概率分布
            Jc=1;
            for k=1:n
                if isempty(find(visited==k, 1))   %开始时置0
                    Wait_point(Jc)=k;
                    Jc=Jc+1;                         %访问的结点个数自加1
                end
            end
            %计算当前位置结点到待选结点的概率
            for k=1:length(Wait_point)
                P(k)=(Tau(visited(end),Wait_point(k))^Alpha)*(Eta(visited(end),Wait_point(k))^Beta);
            end
            P=P/(sum(P));
            %按概率原则选取下一个结点
            Pcum=cumsum(P);     %cumsum，元素累加即求和，使Pcum的值总有大于rand的数
            Sel=find(Pcum>=rand); %若下一节点概率大于目前概率就选择下一结点
            to_visit=Wait_point(Sel(1));
            Tabu(i,j)=to_visit;%保留目前结点路径
        end
    end
    if NC>=2    %保留当前最佳路线
        Tabu(1,:)=Route_Best(NC-1,:);
    end
    %%记录本次迭代最佳路线-------------------------------------------------
    L=zeros(m,1);     %开始距离为0，m*1的列向量
    for i=1:m
        R=Tabu(i,:);
        for j=1:(n-1)
            L(i)=L(i)+D(R(j),R(j+1));    %原距离加上第j个结点到第j+1个结点的距离
        end
        L(i)=L(i)+D(R(1),R(n));      %一轮下来后走过的距离
    end
    Length_Best(NC)=min(L);           %最佳距离取最小
    pos=find(L==Length_Best(NC));
    Route_Best(NC,:)=Tabu(pos(1),:); %此轮迭代后的最佳路线
    L_ave(NC)=mean(L);           %此轮迭代后的平均距离
    NC=NC+1;                      %迭代继续


    %%更新信息素-----------------------------------------------------------
    Delta_Tau=zeros(n,n);        %开始时信息素为n*n的0矩阵
    for i=1:m
        for j=1:(n-1)
            Delta_Tau(Tabu(i,j),Tabu(i,j+1))=Delta_Tau(Tabu(i,j),Tabu(i,j+1))+Q/L(i);
            %此次循环在路径（i，j）上的信息素增量
        end
        Delta_Tau(Tabu(i,n),Tabu(i,1))=Delta_Tau(Tabu(i,n),Tabu(i,1))+Q/L(i);
        %此次循环在整个路径上的信息素增量
    end
    Tau=(1-Rho).*Tau+Delta_Tau; %考虑信息素挥发，更新后的信息素
    %%禁忌表清零
    Tabu=zeros(m,n);             %%直到最大迭代次数
end
%%输出结果
Postion=find(Length_Best==min(Length_Best)); %找到最佳路径（非0为真）
Shortest_Route=Route_Best(Postion(1),:); %最大迭代次数后最佳路径
Shortest_Length=Length_Best(Postion(1)); %最大迭代次数后最短距离

for i=1:n
    Best_Route(i,1) = Coordinate(Shortest_Route(i),1);
    Best_Route(i,2) = Coordinate(Shortest_Route(i),2);
end

%%绘图---------------------------------------------------------------------
figure(2)   %绘制第二个子图
plot(Length_Best)
xlabel('迭代次数')
ylabel('最佳距离')
title('适应度进化曲线')

figure(1)   %绘制第一个子图形
subplot(1,1,1)            
scatter(Coordinate(:,1),Coordinate(:,2));   %绘制散点图
 hold on
 plot([Coordinate(Shortest_Route(1),1),Coordinate(Shortest_Route(n),1)],[Coordinate(Shortest_Route(1),2),Coordinate(Shortest_Route(n),2)],'g')
 text(Coordinate(Shortest_Route(1),1),Coordinate(Shortest_Route(1),2),'起点')
 text(Coordinate(Shortest_Route(n),1),Coordinate(Shortest_Route(n),2),'终点')
 hold on
for ii=2:n
    plot([Coordinate(Shortest_Route(ii-1),1),Coordinate(Shortest_Route(ii),1)],[Coordinate(Shortest_Route(ii-1),2),Coordinate(Shortest_Route(ii),2)],'g')
     hold on
end
xlabel('经度');
ylabel('纬度');
title('外卖配送问题优化结果路线图 ')













