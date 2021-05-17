%%ԭʼ����ϵͳ����Ⱥ�㷨
%%��ջ�������
clear all; %������б���
close all; %��ͼ
clc ;      %����

%%��ʼ������----------------------------------------------------------
m=50;       %% m ���ϸ���
Alpha=1;    %% Alpha ��Ϣ����Ҫ�̶�����
Beta=5;     %% Beta ����������Ҫ�̶�����
Rho=0.1;    %% Rho ��Ϣ�ػӷ�ϵ��
NC_max=200; %%����������
Q=100;      %%����ѭ��һ���ͷŵ���Ϣ������

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
%�������

%%���������໥���룬��ʼ������-------------------------------------------
n = size(Coordinate,1);
D = zeros(n,n);%������·��Ȩֵ�ľ���

%%����·�����벢��ֵ--------------------------------------------------------
for i=1:n
    for j=1:n
        if i~=j
            D(i,j)=((Coordinate(i,1)-Coordinate(j,1))^2+(Coordinate(i,2)-Coordinate(j,2))^2)^0.5;
        else
            D(i,j)=eps;      %i=jʱ�����㣬���ں������������Ҫȡ��������eps��������Ծ��ȣ���ʾ
        end
        D(j,i)=D(i,j);   %�Գƾ���
    end
end
%%������·�������йص���Ϣ--------------------------------------------------
Eta=1./D;          %EtaΪ����������������Ϊ����ĵ���
Tau=ones(n,n);     %TauΪ��Ϣ�ؾ���
Tabu=zeros(m,n);   %�洢����¼·�������ɣ����ɱ�
NC=1;               %��������������¼��������
Route_Best=zeros(NC_max,n);       %ÿ�����·��
Length_Best=inf.*ones(NC_max,1);   %ÿ�����·�ߵĳ���
L_ave=zeros(NC_max,1);        %ÿ��·�ߵ�ƽ������

%%�����㷨����------------------------------------------------------------
while NC<=NC_max        %ֹͣ����֮һ���ﵽ������������ֹͣ
    %%��mֻ��������ŵ�n�������
    Randpos=[];   %�漴��ȡ�����ڴ��ÿֻ���ϳ�ʼ���λ��
    
    %%ȷ��mֻ��������ı�����ÿ�ν�nֻ���Ͼ��ŵ�n����㣬ֱ��mֻ����ȫ����������
    for i=1:(ceil(m/n)) %����ȡ��
        Randpos=[Randpos,randperm(n)];
        %randperm(n)����1��n�����������ظ������������䣬����һ����ά����
    end
    Tabu(:,1)=(Randpos(1,1:m))';   %���������ڽ�����ڵ�һ��
    
    %%mֻ���ϰ����ʺ���ѡ����һ���
    for j=2:n     %ʣ�������������������ڵĽ�㲻����
        for i=1:m
            visited=Tabu(i,1:(j-1)); %��¼�ѷ��ʹ��Ľ��
            Wait_point=zeros(1,(n-j+1));       %��ǰ���ϴ����ʵĽ��
            P=Wait_point;                      %���ô����ʽ���ѡ����ʷֲ�
            Jc=1;
            for k=1:n
                if isempty(find(visited==k, 1))   %��ʼʱ��0
                    Wait_point(Jc)=k;
                    Jc=Jc+1;                         %���ʵĽ������Լ�1
                end
            end
            %���㵱ǰλ�ý�㵽��ѡ���ĸ���
            for k=1:length(Wait_point)
                P(k)=(Tau(visited(end),Wait_point(k))^Alpha)*(Eta(visited(end),Wait_point(k))^Beta);
            end
            P=P/(sum(P));
            %������ԭ��ѡȡ��һ�����
            Pcum=cumsum(P);     %cumsum��Ԫ���ۼӼ���ͣ�ʹPcum��ֵ���д���rand����
            Sel=find(Pcum>=rand); %����һ�ڵ���ʴ���Ŀǰ���ʾ�ѡ����һ���
            to_visit=Wait_point(Sel(1));
            Tabu(i,j)=to_visit;%����Ŀǰ���·��
        end
    end
    if NC>=2    %������ǰ���·��
        Tabu(1,:)=Route_Best(NC-1,:);
    end
    %%��¼���ε������·��-------------------------------------------------
    L=zeros(m,1);     %��ʼ����Ϊ0��m*1��������
    for i=1:m
        R=Tabu(i,:);
        for j=1:(n-1)
            L(i)=L(i)+D(R(j),R(j+1));    %ԭ������ϵ�j����㵽��j+1�����ľ���
        end
        L(i)=L(i)+D(R(1),R(n));      %һ���������߹��ľ���
    end
    Length_Best(NC)=min(L);           %��Ѿ���ȡ��С
    pos=find(L==Length_Best(NC));
    Route_Best(NC,:)=Tabu(pos(1),:); %���ֵ���������·��
    L_ave(NC)=mean(L);           %���ֵ������ƽ������
    NC=NC+1;                      %��������


    %%������Ϣ��-----------------------------------------------------------
    Delta_Tau=zeros(n,n);        %��ʼʱ��Ϣ��Ϊn*n��0����
    for i=1:m
        for j=1:(n-1)
            Delta_Tau(Tabu(i,j),Tabu(i,j+1))=Delta_Tau(Tabu(i,j),Tabu(i,j+1))+Q/L(i);
            %�˴�ѭ����·����i��j���ϵ���Ϣ������
        end
        Delta_Tau(Tabu(i,n),Tabu(i,1))=Delta_Tau(Tabu(i,n),Tabu(i,1))+Q/L(i);
        %�˴�ѭ��������·���ϵ���Ϣ������
    end
    Tau=(1-Rho).*Tau+Delta_Tau; %������Ϣ�ػӷ������º����Ϣ��
    %%���ɱ�����
    Tabu=zeros(m,n);             %%ֱ������������
end
%%������
Postion=find(Length_Best==min(Length_Best)); %�ҵ����·������0Ϊ�棩
Shortest_Route=Route_Best(Postion(1),:); %���������������·��
Shortest_Length=Length_Best(Postion(1)); %��������������̾���

for i=1:n
    Best_Route(i,1) = Coordinate(Shortest_Route(i),1);
    Best_Route(i,2) = Coordinate(Shortest_Route(i),2);
end

%%��ͼ---------------------------------------------------------------------
figure(2)   %���Ƶڶ�����ͼ
plot(Length_Best)
xlabel('��������')
ylabel('��Ѿ���')
title('��Ӧ�Ƚ�������')

figure(1)   %���Ƶ�һ����ͼ��
subplot(1,1,1)            
scatter(Coordinate(:,1),Coordinate(:,2));   %����ɢ��ͼ
 hold on
 plot([Coordinate(Shortest_Route(1),1),Coordinate(Shortest_Route(n),1)],[Coordinate(Shortest_Route(1),2),Coordinate(Shortest_Route(n),2)],'g')
 text(Coordinate(Shortest_Route(1),1),Coordinate(Shortest_Route(1),2),'���')
 text(Coordinate(Shortest_Route(n),1),Coordinate(Shortest_Route(n),2),'�յ�')
 hold on
for ii=2:n
    plot([Coordinate(Shortest_Route(ii-1),1),Coordinate(Shortest_Route(ii),1)],[Coordinate(Shortest_Route(ii-1),2),Coordinate(Shortest_Route(ii),2)],'g')
     hold on
end
xlabel('����');
ylabel('γ��');
title('�������������Ż����·��ͼ ')













