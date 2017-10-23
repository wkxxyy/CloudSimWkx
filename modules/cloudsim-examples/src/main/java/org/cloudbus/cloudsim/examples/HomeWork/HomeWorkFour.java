package org.cloudbus.cloudsim.examples.HomeWork;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.*;

public class HomeWorkFour {

    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmlist;

    public static void main(String[] args) {
        Log.printLine("Starting CloudSimExample1...");

        try {

            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;
            CloudSim.init(num_user, calendar, trace_flag);


            Datacenter datacenter0 = createDatacenter("Datacenter_0");


            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();


            vmlist = new ArrayList<Vm>();

            int pesNumber = 1;

            for(int i=0;i<10;i++){//新建对应个vm，100/10就是10个虚拟机
                int vmid=i;
                int mips =1000;
                long size =10000;
                int ram =512; // vm memory (MB)
                long bw = 1000;
                pesNumber =1; // number of cpus
                String vmm = "Xen"+i; // VMM name

                Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
                vmlist.add(vm);
            }

            broker.submitVmList(vmlist);

            cloudletList = new ArrayList<Cloudlet>();
            Random random=new Random();
            // Cloudlet properties

            UtilizationModel utilizationModel = new UtilizationModelFull();
            int vmindex=0;
            for (int j=0;j<10;j++){//十个任务

                int id=j;
                long length =random.nextInt(5000);//任务大小服从均匀分布
                long fileSize =300;
                long outputSize =300;

                Cloudlet cloudlet =
                        new Cloudlet(id, length, pesNumber, fileSize,
                                outputSize, utilizationModel, utilizationModel,
                                utilizationModel);
                cloudlet.setUserId(brokerId);//把新建的任务和一个broker绑定起来，只有需要broker请求center创建这个vm
                cloudlet.setVmId(vmlist.get(vmindex).getId());//把一个任务和broker中的vmlist中的一个vm绑定起来
                vmindex++;
                // add the cloudlet to the list
                cloudletList.add(cloudlet);

            }


            // submit cloudlet list to the broker
            broker.submitCloudletList(cloudletList);

            // Sixth step: Starts the simulation
            CloudSim.startSimulation();

            CloudSim.stopSimulation();

            //Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);

            Log.printLine("HomeWorkFour finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }


    }

    /**
     * Creates the datacenter.
     *
     * @param name the name
     *
     * @return the datacenter
     */
    private static Datacenter createDatacenter(String name) {

        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store
        // our machine
        List<Host> hostList = new ArrayList<Host>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.//Cpu的核心
        List<Pe> peList = new ArrayList<Pe>();

        int mips = 10000;

        //4个核心
        peList.add(new Pe(0, new PeProvisionerSimple(mips)));
        peList.add(new Pe(1, new PeProvisionerSimple(mips)));
        peList.add(new Pe(2, new PeProvisionerSimple(mips)));
        peList.add(new Pe(3, new PeProvisionerSimple(mips)));

        //两个主机的属性
        int hostId0 = 0;
        int ram0 = 20480; // host memory (MB)
        long storage0 = 10000000; // host storage
        int bw0 = 100000;

        int hostId1 = 1;
        int ram1 = 20480; // host memory (MB)
        long storage1 = 10000000; // host storage
        int bw1 = 100000;

        //添加两个主机属性
        hostList.add(
                new Host(
                        hostId0,
                        new RamProvisionerSimple(ram0),
                        new BwProvisionerSimple(bw0),
                        storage0,
                        peList,
                        new VmSchedulerTimeShared(peList)//核心分配策略，让vm是否能够共享其他的核心
                )
        );

        hostList.add(
                new Host(
                        hostId1,
                        new RamProvisionerSimple(ram1),
                        new BwProvisionerSimple(bw1),
                        storage1,
                        peList,
                        new VmSchedulerTimeShared(peList)//核心分配策略，让vm是否能够共享其他的核心
                )
        );


        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
        // devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(//数据中心的特征
                arch, os, vmm, hostList, time_zone, cost, costPerMem,
                costPerStorage, costPerBw);

        // 6. Finally, we need to create a PowerDatacenter object.
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    // We strongly encourage users to develop their own broker policies, to
    // submit vms and cloudlets according
    // to the specific rules of the simulated scenario
    /**
     * Creates the broker.
     *
     * @return the datacenter broker
     */
    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    /**
     * Prints the Cloudlet objects.
     *
     * @param list list of Cloudlets
     */
    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "VM ID" + indent + "Time" + indent
                + "Start Time" + indent + "Finish Time"+indent+"cloudLetLength");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId()
                        + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent
                        + dft.format(cloudlet.getActualCPUTime()) + indent
                        + indent + dft.format(cloudlet.getExecStartTime())
                        + indent + indent
                        + dft.format(cloudlet.getFinishTime())+indent+indent+indent+dft.format(cloudlet.getCloudLength()));
            }
        }
    }
}
