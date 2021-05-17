function [Shortest_Route] = Ant_Colony_Algorithm_2(Business,Custerm)
%%���ڴ������Ⱥ�㷨
%%��ջ�������
%%��ʼ������----------------------------------------------------------
m=50;       %% m ���ϸ���
Alpha=1;    %% Alpha ��Ϣ����Ҫ�̶�����
Beta=5;     %% Beta ����������Ҫ�̶�����
Rho=0.1;    %% Rho ��Ϣ�ػӷ�ϵ��
NC_max=200; %%����������
Q=100;      %%����ѭ��һ���ͷŵ���Ϣ������
Coordinate_Best = [];
visited = [];
Wait_point = [];

Coordinate_B = Business;%�̼�����

Coordinate_C = Custerm;%�˿�����
%�������

%%���������������໥���룬��ʼ������
for i = 1:m %(a,b,i)��iֻ���ϵĴ���������
    Coordinate(:,:,i) = Coordinate_B;
end
n = size(Coordinate,1);

Coordinate_Sum = Coordinate;
D = zeros(n,n,m);%������·��Ȩֵ�ľ���,���������
D = distance(size(Coordinate,1),Coordinate,m);

%%������·�������йص���Ϣ-------------------------------------------------
Eta=1./D;          %EtaΪ����������������Ϊ����ĵ���
Tau=ones(n,n);     %TauΪ��Ϣ�ؾ���
Tabu=zeros(m,2*n);   %�洢����¼·�������ɣ����ɱ�
NC=1;               %��������������¼��������
Length_Best=inf.*ones(NC_max,1);   %ÿ�����·�ߵĳ���
L_ave=zeros(NC_max,1);        %ÿ��·�ߵ�ƽ������

while NC<=NC_max
    %%��mֻ��������ŵ�n�������
    Randpos=[];  
    for i=1:(ceil(m/n)) %����ȡ��
        Randpos=[Randpos,randperm(n)];
    end
    Tabu(:,1)=(Randpos(1,1:m))'; 
    
    for j = 2:2*n
        for i = 1:m
            %��չ���ϴ����ʽ��--------------------------------------
            if IsIn(Coordinate(Tabu(i,(j-1)),:,i),Coordinate_B) ~= 0
                
                 %�ǣ��ҵ���Ӧ�˿����꣬�ж��Ƿ��Ӧ���ϱ����б���
                if IsIn(Coordinate_C(IsIn(Coordinate(Tabu(i,(j-1)),:,i),Coordinate_B),:),Coordinate(:,:,i)) == 0
                    
                    %���ǣ���ӵ��Լ��б��У����ж��Ƿ������������б���
                    if Coordinate(end,:,i) == 0
                        Coordinate(FoundFirstZero(Coordinate(:,:,i)),:,i) = Coordinate_C(IsIn(Coordinate(Tabu(i,(j-1)),:,i),Coordinate_B),:);
                    else
                        Coordinate(end+1,:,i) = Coordinate_C(IsIn(Coordinate(Tabu(i,(j-1)),:,i),Coordinate_B),:);
                    end
                    %-------------------------------------------------
                    %���¼���ڵ����
                    D = distance(size(Coordinate,1),Coordinate,m);
                    %������·���й���Ϣ
                    Eta=1./D;          %EtaΪ����������������Ϊ����ĵ���
                    
                    %���ǣ�����չ��Ϣ�ؾ���
                    if IsIn_AntPoint(Coordinate_C(IsIn(Coordinate(Tabu(i,(j-1)),:,i),Coordinate_B),:),Coordinate_Sum,i,m) == 0
                        Coordinate_Sum = Coordinate;
                        Tau(end+1,:) = 1;
                        Tau(:,end+1) = 1;
                    end
                end   
            end
            
            
            %--------------------------------------------------------
            visited=Tabu(i,1:(j-1)); %��¼�ѷ��ʹ��Ľ��
            Wait_point = zeros(1,(size(Coordinate,1)-j+1));       %�����ʵĽ��
             P=Wait_point;                      %���ô����ʽ���ѡ����ʷֲ�
            %?????--------------------------------------------------------------
            Jc=1;
            for k=1:size(Coordinate,1)
                if isempty(find(visited==k, 1))   %��ʼʱ��0
                    Wait_point(Jc)=k;
                    Jc=Jc+1;                         %���ʵĽ������Լ�1
                end
            end
            %------------------------------------------------------------
            %���㵱ǰλ�ý�㵽��ѡ���ĸ���----------------------------
            for k=1:length(Wait_point)
                P(k)=(Tau(visited(end),Wait_point(k))^Alpha)*(Eta(visited(end),Wait_point(k),i)^Beta);
            end
            P=P/(sum(P));
             %������ԭ��ѡȡ��һ�����
            Pcum=cumsum(P);     %cumsum��Ԫ���ۼӼ���ͣ�ʹPcum��ֵ���д���rand����
            Sel=find(Pcum>=rand); %����һ�ڵ���ʴ���Ŀǰ���ʾ�ѡ����һ���
            to_visit=Wait_point(Sel(1));
            Tabu(i,j)=to_visit;%����Ŀǰ���·��
            %------------------------------------------------------------
        end
    end
        %%��¼���ε������·��-------------------------------------------------
        L=zeros(m,1);     %��ʼ����Ϊ0��m*1��������
        for i=1:m
            R=Tabu(i,:);
            for j=1:(size(Coordinate,1)-1)
                L(i)=L(i)+D(R(j),R(j+1));    %ԭ������ϵ�j����㵽��j+1�����ľ���
            end
            L(i)=L(i)+D(R(1),R(n));      %һ���������߹��ľ���
        end
        Length_Best(NC)=min(L);           %��Ѿ���ȡ��С
        pos=find(L==Length_Best(NC));
        %����ÿһ����������·�������-------------------
         Coordinate_Best(:,:,NC) = Coordinate(:,:,pos(1));
        
        L_ave(NC)=mean(L);           %���ֵ������ƽ������
        NC=NC+1;                      %��������
        
    %%������Ϣ��-----------------------------------------------------------
    Delta_Tau=zeros(size(Coordinate,1),size(Coordinate,1));        %��ʼʱ��Ϣ��Ϊn*n��0����
    for i=1:m
        for j=1:(size(Coordinate,1)-1)
            Delta_Tau(Tabu(i,j),Tabu(i,j+1))=Delta_Tau(Tabu(i,j),Tabu(i,j+1))+Q/L(i);
            %�˴�ѭ����·����i��j���ϵ���Ϣ������
        end
        Delta_Tau(Tabu(i,n),Tabu(i,1))=Delta_Tau(Tabu(i,n),Tabu(i,1))+Q/L(i);
        %�˴�ѭ��������·���ϵ���Ϣ������
    end
    Tau=(1-Rho).*Tau+Delta_Tau; %������Ϣ�ػӷ������º����Ϣ��
    %%���ɱ�����
    Tabu=zeros(m,size(Coordinate_B,1));             %%ֱ������������
    
    %��һ�ε���ǰ��ԭĿ���������------------------------------------
    Coordinate_Sum = Coordinate;
    Coordinate = zeros(size(Coordinate_B,1),size(Coordinate_B,2),m);
    for i = 1:m %(a,b,i)��iֻ���ϵĴ���������
        Coordinate(:,:,i) = Coordinate_B;
    end
    %��̬���̣���Ҫ����ÿһ�������·�ߵ������----------------------------
end
%%������
Postion=find(Length_Best==min(Length_Best)); %�ҵ����·������0Ϊ�棩
Shortest_Route=Coordinate_Best(:,:,Postion(1)); %���������������·��
Shortest_Length=Length_Best(Postion(1)); %��������������̾���

%%��ͼ---------------------------------------------------------------------
%figure(2)   %���Ƶڶ�����ͼ
%plot(Length_Best)
%xlabel('��������')
%ylabel('��Ѿ���')
%title('��Ӧ�Ƚ�������')

%figure(1)   %���Ƶ�һ����ͼ��
%subplot(1,1,1)            
%scatter(Shortest_Route(:,1),Shortest_Route(:,2),'r');   %����ɢ��ͼ
 %hold on
 %plot([Shortest_Route(1,1),Shortest_Route(size(Shortest_Route,1),1)],[Shortest_Route(1,2),Shortest_Route(size(Shortest_Route,1),2)],'g')
 %text(Shortest_Route(1,1),Shortest_Route(1,2),'���')
 %text(Shortest_Route(size(Shortest_Route,1),1),Shortest_Route(size(Shortest_Route,1),2),'�յ�')
% hold on
%for ii=2:size(Shortest_Route)
%    plot([Shortest_Route(ii-1,1),Shortest_Route(ii,1)],[Shortest_Route(ii-1,2),Shortest_Route(ii,2)],'g')
%    text(Shortest_Route(ii,1),Shortest_Route(ii,2),num2str(ii))
%    hold on
%end
%xlabel('����');
%ylabel('γ��');
%title('�������������Ż����·��ͼ ')
%------------------------------------------------------------------------
end
