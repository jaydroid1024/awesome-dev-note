

/*
题目：【002-LeetCode-题解-两数相加-链表】
地址：https://leetcode-cn.com/problems/add-two-numbers/
*/










/*
一，题目描述：
 给你两个 非空 的链表，表示两个非负的整数。
 它们每位数字都是按照 逆序 的方式存储的，
 并且每个节点只能存储 一位 数字。
 请你将两个数相加，并以相同形式返回一个表示他们和的链表。
 你可以假设除了数字 0 之外，这两个数都不会以 0 开头。

 示例：
 输入：l1 = [2,4,3], l2 = [5,6,4]
 输出：[7,0,8]
 解释：342 + 465 = 807.

 提示：
 每个链表中的节点数在范围 [1, 100] 内
 0 <= Node.val <= 9
 题目数据保证列表表示的数字不含前导零

 Related Topics 递归 链表 数学
 👍 6152 👎 0

*/


/**
 * Definition for singly-linked list.
 * public class ListNode {
 * int val;
 * ListNode next;
 * ListNode() {}
 * ListNode(int val) { this.val = val; }
 * ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    /*
    二，解题思路
    示例：输入：l1 = [2,4,3], l2 = [5,6,4] 输出：[7,0,8]
    1. 从个位开始逐位计算求和，考虑进位的情况
    2. 位数少的数对应位不存在就补0
    3. 得到当前位，记录本次进位，继续下一次循环
    4. 循环结束后考虑最后一次相加时是否有进位
     */


    /*
     三，代码实现

     */
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        //最终结果的头指针，默认赋值一个空的节点
        ListNode result_head = new ListNode(-1);
        //最终结果的尾指针，默认指向结果的头节点
        ListNode result_tail = result_head;
        //每位数字相加的进位，默认为0
        int carry = 0;
        //进入循环的条件：满足任一
        // l1没有移动到尾部
        // l2没有移动到尾部
        while (l1 != null || l2 != null) {
            //取出l1节点中的数字，不存在就补0
            int n1 = l1 != null ? l1.val : 0;
            //取出l2节点中的数字，不存在就补0
            int n2 = l2 != null ? l2.val : 0;
            //求和
            int sum = n1 + n2 + carry;
            //得到当前位并组装节点
            result_tail.next = new ListNode(sum % 10);
            //尾节点后移继续下次组装
            result_tail = result_tail.next;
            //记录本次进位
            carry = sum / 10;
            //继续下一次循环
            if (l1 != null) {
                l1 = l1.next;
            }
            if (l2 != null) {
                l2 = l2.next;
            }
        }
        //循环结束后考虑最后一次是否有进位
        if (carry > 0) {
            result_tail.next = new ListNode(carry);
        }
        //返回最终结果
        return result_head.next;

    }

















     /*
     三，复杂度分析
     时间复杂度：O(max(m,n)), 其中 m 和 n 分别为两个链表的长度
     空间复杂度：O(1)

     */
}









    /*
     四，验证结果
     题目地址：https://leetcode-cn.com/problems/add-two-numbers/

     */




















