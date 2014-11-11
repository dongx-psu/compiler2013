########################################
############### CODE GEN ###############
########################################
	.data
	.align 2
	.globl args
args:	.space 4
	.align 2
	.text
	.align 2
	.globl main
main:
	la $v1, args
	jal run_main
	li $v0, 10
	syscall
	jr $ra
run_main:
	addiu $sp, $sp, -228
	sw $ra 224($sp)
	sw $s4, 196($sp)
	sw $s3, 192($sp)
	sw $s2, 188($sp)
	sw $s1, 184($sp)
	sw $s0, 180($sp)
	sw $t7, 176($sp)
	sw $t6, 172($sp)
	sw $t5, 168($sp)
	sw $t4, 164($sp)
	sw $t3, 160($sp)
	sw $t2, 156($sp)
	sw $t1, 152($sp)
	sw $t0, 148($sp)
	li $t6, 36
	move $a0, $t6
	jal malloc
	move $t3, $v0
	li $t7, 36
	move $a0, $t7
	jal malloc
	move $t1, $v0
	li $t6, 36
	move $a0, $t6
	jal malloc
	move $t4, $v0
	li $t5, 0
L0:
	bge $t5, 3, L1
	li $t2, 0
L2:
	bge $t2, 3, L3
	li $t0, 0
L4:
	bge $t0, 3, L5
	mul $t6, $t5, 12 #I
	addu $t7, $t4, $t6
	sll $s0, $t0, 2 #SR
	addu $s1, $t7, $s0
	lw $s2, 0($s1)
	addu $t7, $t3, $t6
	sll $s3, $t2, 2 #SR
	addu $t6, $t7, $s3
	lw $s4, 0($t6)
	mul $t7, $t2, 12 #I
	addu $t6, $t1, $t7
	addu $s3, $t6, $s0
	lw $t7, 0($s3)
	mul $t6, $s4, $t7
	addu $s0, $s2, $t6
	sw $s0, 0($s1)
	addiu $t0, $t0, 1 #I
	j L4
L5:
	addiu $t2, $t2, 1 #I
	j L2
L3:
	addiu $t5, $t5, 1 #I
	j L0
L1:
	li $t0, 0
	move $v0, $t0
	j run_main_leave
run_main_leave:
	lw $s4, 196($sp)
	lw $s3, 192($sp)
	lw $s2, 188($sp)
	lw $s1, 184($sp)
	lw $s0, 180($sp)
	lw $t7, 176($sp)
	lw $t6, 172($sp)
	lw $t5, 168($sp)
	lw $t4, 164($sp)
	lw $t3, 160($sp)
	lw $t2, 156($sp)
	lw $t1, 152($sp)
	lw $t0, 148($sp)
	lw $ra, 224($sp)
	addiu $sp, $sp, 228
	jr $ra
########################################
############# RUNTIME CODE #############
########################################
