function [Shortest_Route] = Ant_Colony_Algorithm_2(Business,Custerm)
%%用于打包的蚁群算法
%%清空环境变量
%%初始化参数----------------------------------------------------------
m=50;       %% m 蚂蚁个数
Alpha=1;    %% Alpha 信息素重要程度因子
Beta=5;     %% Beta 启发函数重要程度因子
Rho=0.1;    %% Rho 信息素挥发系数
NC_max=200; %%最大迭代次数
Q=100;      %%蚂蚁循环一次释放的信息素总量
Coordinate_Best = [];
visited = [];
Wait_point = [];

Coordinate_B = Business;%商家坐标

Coordinate_C = Custerm;%顾客坐标
%坐标矩阵

%%计算待遍历结点间的相互距离，初始化数组
for i = 1:m %(a,b,i)第i只蚂蚁的待访问坐标
    Coordinate(:,:,i) = Coordinate_B;
end
n = size(Coordinate,1);

Coordinate_Sum = Coordinate;
D = zeros(n,n,m);%创建赋路径权值的矩阵,并计算距离
D = distance(size(Coordinate,1),Coordinate,m);

%%设置与路径长度有关的信息-------------------------------------------------
Eta=1./D;          %Eta为启发函数，这里设为距离的倒数
Tau=ones(n,n);     %Tau为信息素矩阵
Tabu=zeros(m,2*n);   %存储并记录路径的生成，禁忌表
NC=1;               %迭代计数器，记录迭代次数
Length_Best=inf.*ones(NC_max,1);   %每代最佳路线的长度
L_ave=zeros(NC_max,1);        %每代路线的平均长度

while NC<=NC_max
    %%将m只蚂蚁随机放到n个结点上
    Randpos=[];  
    for i=1:(ceil(m/n)) %向上取整
        Randpos=[Randpos,randperm(n)];
    end
    Tabu(:,1)=(Randpos(1,1:m))'; 
    
    for j = 2:2*n
        for i = 1:m
            %拓展蚂蚁待访问结点--------------------------------------
            if IsIn(Coordinate(Tabu(i,(j-1)),:,i),Coordinate_B) ~= 0
                
                 %是，找到对应顾客坐标，判断是否对应蚂蚁遍历列表中
                if IsIn(Coordinate_C(IsIn(Coordinate(Tabu(i,(j-1)),:,i),Coordinate_B),:),Coordinate(:,:,i)) == 0
                    
                    %不是，添加到自己列表中，并判断是否在其他蚂蚁列表中
                    if Coordinate(end,:,i) == 0
                        Coordinate(FoundFirstZero(Coordinate(:,:,i)),:,i) = Coordinate_C(IsIn(Coordinate(Tabu(i,(j-1)),:,i),Coordinate_B),:);
                    else
                        Coordinate(end+1,:,i) = Coordinate_C(IsIn(Coordinate(Tabu(i,(j-1)),:,i),Coordinate_B),:);
                    end
                    %-------------------------------------------------
                    %重新计算节点距离
                    D = distance(size(Coordinate,1),Coordinate,m);
                    %更新与路径有关信息
                    Eta=1./D;          %Eta为启发函数，这里设为距离的倒数
                    
                    %不是，则扩展信息素矩阵
                    if IsIn_AntPoint(Coordinate_C(IsIn(Coordinate(Tabu(i,(j-1)),:,i),Coordinate_B),:),Coordinate_Sum,i,m) == 0
                        Coordinate_Sum = Coordinate;
                        Tau(end+1,:) = 1;
                        Tau(:,end+1) = 1;
                    end
                end   
            end
            
            
            %--------------------------------------------------------
            visited=Tabu(i,1:(j-1)); %记录已访问过的结点
            Wait_point = zeros(1,(size(Coordinate,1)-j+1));       %待访问的结点
             P=Wait_point;                      %设置待访问结点的选择概率分布
            %?????--------------------------------------------------------------
            Jc=1;
            for k=1:size(Coordinate,1)
                if isempty(find(visited==k, 1))   %开始时置0
                    Wait_point(Jc)=k;
                    Jc=Jc+1;                         %访问的结点个数自加1
                end
            end
            %------------------------------------------------------------
            %计算当前位置结点到待选结点的概率----------------------------
            for k=1:length(Wait_point)
                P(k)=(Tau(visited(end),Wait_point(k))^Alpha)*(Eta(visited(end),Wait_point(k),i)^Beta);
            end
            P=P/(sum(P));
             %按概率原则选取下一个结点
            Pcum=cumsum(P);     %cumsum，元素累加即求和，使Pcum的值总有大于rand的数
            Sel=find(Pcum>=rand); %若下一节点概率大于目前概率就选择下一结点
            to_visit=Wait_point(Sel(1));
            Tabu(i,j)=to_visit;%保留目前结点路径
            %------------------------------------------------------------
        end
    end
        %%记录本次迭代最佳路线-------------------------------------------------
        L=zeros(m,1);     %开始距离为0，m*1的列向量
        for i=1:m
            R=Tabu(i,:);
            for j=1:(size(Coordinate,1)-1)
                L(i)=L(i)+D(R(j),R(j+1));    %原距离加上第j个结点到第j+1个结点的距离
            end
            L(i)=L(i)+D(R(1),R(n));      %一轮下来后走过的距离
        end
        Length_Best(NC)=min(L);           %最佳距离取最小
        pos=find(L==Length_Best(NC));
        %保存每一代坐标点最佳路线坐标点-------------------
         Coordinate_Best(:,:,NC) = Coordinate(:,:,pos(1));
        
        L_ave(NC)=mean(L);           %此轮迭代后的平均距离
        NC=NC+1;                      %迭代继续
        
    %%更新信息素-----------------------------------------------------------
    Delta_Tau=zeros(size(Coordinate,1),size(Coordinate,1));        %开始时信息素为n*n的0矩阵
    for i=1:m
        for j=1:(size(Coordinate,1)-1)
            Delta_Tau(Tabu(i,j),Tabu(i,j+1))=Delta_Tau(Tabu(i,j),Tabu(i,j+1))+Q/L(i);
            %此次循环在路径（i，j）上的信息素增量
        end
        Delta_Tau(Tabu(i,n),Tabu(i,1))=Delta_Tau(Tabu(i,n),Tabu(i,1))+Q/L(i);
        %此次循环在整个路径上的信息素增量
    end
    Tau=(1-Rho).*Tau+Delta_Tau; %考虑信息素挥发，更新后的信息素
    %%禁忌表清零
    Tabu=zeros(m,size(Coordinate_B,1));             %%直到最大迭代次数
    
    %下一次迭代前还原目标遍历数组------------------------------------
    Coordinate_Sum = Coordinate;
    Coordinate = zeros(size(Coordinate_B,1),size(Coordinate_B,2),m);
    for i = 1:m %(a,b,i)第i只蚂蚁的待访问坐标
        Coordinate(:,:,i) = Coordinate_B;
    end
    %动态过程，需要保存每一代的最佳路线的坐标点----------------------------
end
%%输出结果
Postion=find(Length_Best==min(Length_Best)); %找到最佳路径（非0为真）
Shortest_Route=Coordinate_Best(:,:,Postion(1)); %最大迭代次数后最佳路径
Shortest_Length=Length_Best(Postion(1)); %最大迭代次数后最短距离

%%绘图---------------------------------------------------------------------
%figure(2)   %绘制第二个子图
%plot(Length_Best)
%xlabel('迭代次数')
%ylabel('最佳距离')
%title('适应度进化曲线')

%figure(1)   %绘制第一个子图形
%subplot(1,1,1)            
%scatter(Shortest_Route(:,1),Shortest_Route(:,2),'r');   %绘制散点图
 %hold on
 %plot([Shortest_Route(1,1),Shortest_Route(size(Shortest_Route,1),1)],[Shortest_Route(1,2),Shortest_Route(size(Shortest_Route,1),2)],'g')
 %text(Shortest_Route(1,1),Shortest_Route(1,2),'起点')
 %text(Shortest_Route(size(Shortest_Route,1),1),Shortest_Route(size(Shortest_Route,1),2),'终点')
% hold on
%for ii=2:size(Shortest_Route)
%    plot([Shortest_Route(ii-1,1),Shortest_Route(ii,1)],[Shortest_Route(ii-1,2),Shortest_Route(ii,2)],'g')
%    text(Shortest_Route(ii,1),Shortest_Route(ii,2),num2str(ii))
%    hold on
%end
%xlabel('经度');
%ylabel('纬度');
%title('外卖配送问题优化结果路线图 ')
%------------------------------------------------------------------------
end
