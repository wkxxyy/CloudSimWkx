1：第三次作业编程：用一种冒泡排序的方法在Broker提交任务之前对要提交的CLoudList进行了一个排序，将最小任务长度的任务放在了提交列表的最前面，这样就能做到最小任务长度调度，排序算法在DataCenterBroker下的submitCloudlets（）方法中：
排序代码也可以看下面，运行第四次作业就可以看到使用这种调度算法的结果，运行程序的顺序都是按照任务最短而来的。
List<Cloudlet> sortList=new ArrayList<Cloudlet>();
		ArrayList<Cloudlet> tempList=new ArrayList<Cloudlet>();
		for (Cloudlet cloudlet : getCloudletList()) {
			tempList.add(cloudlet);
		}
		int totalCloudlets=tempList.size();
		for (int i=0;i<totalCloudlets;i++){
			Cloudlet smallestCloudlets=tempList.get(0);
			for (Cloudlet checkCloudlet:tempList){
				if (smallestCloudlets.getCloudLength()>checkCloudlet.getCloudLength()){
					smallestCloudlets=checkCloudlet;
				}
			}
			sortList.add(smallestCloudlets);
			tempList.remove(smallestCloudlets);
		}


2：第四次作业编程请打开cloudsim-cloudsim-4.0\modules\cloudsim-examples\src\main\java\org\cloudbus\cloudsim\examples\HomeWork\HomeWorkFour.java文件运行即可